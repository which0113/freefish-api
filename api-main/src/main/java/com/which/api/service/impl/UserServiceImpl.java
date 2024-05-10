package com.which.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.which.api.manager.RedissonManager;
import com.which.api.mapper.UserMapper;
import com.which.api.model.dto.user.UserLoginRequest;
import com.which.api.model.dto.user.UserRegisterRequest;
import com.which.api.model.entity.User;
import com.which.api.model.enums.UserRoleEnum;
import com.which.api.model.enums.UserStatusEnum;
import com.which.api.model.vo.UserLoginVO;
import com.which.api.service.UserService;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import com.which.apicommon.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.which.api.constant.CommonConstant.CHECKIN_BALANCE;
import static com.which.api.constant.RedisConstant.*;
import static com.which.api.constant.UserConstant.*;

/**
 * 用户服务实现
 *
 * @author which
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedissonManager redissonManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

/*    @Resource
    private ApiMsgProducer apiMsgProducer;*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String userName = userRegisterRequest.getUserName();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String invitationCode = userRegisterRequest.getInvitationCode();

        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userName.length() > 40) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称过长");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //  5. 账户不包含特殊字符
        // 匹配由数字、小写字母、大写字母组成的字符串,且字符串的长度至少为1个字符
        String pattern = "[0-9a-zA-Z]+";
        if (!userAccount.matches(pattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号由数字、小写字母、大写字母组成");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 账户不能重复
        String redissonLock = (USER_CURD_KEY + "userRegister:" + userAccount).intern();
        return redissonManager.redissonDistributedLocks(redissonLock, () -> {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            User invitationCodeUser;
            if (StringUtils.isNotBlank(invitationCode)) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
                // 可能出现重复invitationCode,查出的不是一条
                invitationCodeUser = this.getOne(userLambdaQueryWrapper);
                if (invitationCodeUser == null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "该邀请码无效");
                }
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // ak/sk
            String accessKey = DigestUtils.md5DigestAsHex((userAccount + SALT + VOUCHER).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + userAccount).getBytes());

            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName(userName);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setInvitationCode(generateRandomString(8));
            user.setUserRole(UserRoleEnum.USER.getValue());
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            Long userId = user.getId();
            // 防止MQ消息发送失败导致事务回滚
//            try {
//                apiMsgProducer.sendMessage(userId);
//                log.info("消息发送MQ成功，userId：{}", userId);
//            } catch (Exception e) {
//                log.error("消息发送MQ失败：{}", e.getMessage());
//            }
            return userId;
        }, "注册账号失败");
    }

    @Override
    public boolean userCheckIn(HttpServletRequest request) {
        UserVO loginUser = getLoginUser(request);
        Long userId = loginUser.getId();
        String checkInKey = USER_CHECK_IN + userId;
        Boolean flag = redisTemplate.hasKey(checkInKey);
        if (Boolean.TRUE.equals(flag)) {
            return false;
        }

        // 设置 checkInKey 的过期时间，凌晨0点清空
        Date currentDate = new Date();
        String day = DateFormatUtils.format(currentDate, "yyyyMMdd");
        // 获取明天凌晨0点的时间
        Date tomorrowZero = DateUtil.beginOfDay(DateUtil.tomorrow());
        // 计算时间差，单位为秒
        long refreshTime = DateUtil.between(currentDate, tomorrowZero, DateUnit.SECOND);
        redisTemplate.opsForValue().set(checkInKey, day, refreshTime, TimeUnit.SECONDS);

        // 签到积分+5
        String redissonLock = (GEN_CHART_BY_AI + "userCheckIn:" + loginUser.getUserAccount()).intern();
        redissonManager.redissonDistributedLocks(redissonLock, () -> {
            boolean update = this.increaseWalletBalance(userId, CHECKIN_BALANCE);
            if (!update) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "签到失败");
            }
            return null;
        }, "签到失败");

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 1 校验
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短,不能小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短,不能低于8位字符");
        }
        // 账户不包含特殊字符，匹配由数字、小写字母、大写字母组成的字符串，且字符串的长度至少为1个字符
        String pattern = "[0-9a-zA-Z]+";
        if (!userAccount.matches(pattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号需由数字、小写字母、大写字母组成");
        }
        // 2 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (user.getStatus().equals(UserStatusEnum.BAN.getValue())) {
            throw new BusinessException(ErrorCode.PROHIBITED, "账号已封禁");
        }
        int update = userMapper.updateById(user);
        if (update == 0) {
            log.error("账号更新异常，userId：{}", user.getId());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录异常");
        }
        // 3 记录用户的登录态到redis
        // 3.1 生成128位的token
        String token = this.generateRandomString(128);
        String tokenKey = USER_LOGIN_KEY + token;
        // 3.2 保存userVO到redis缓存
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        Map<String, Object> userMap = BeanUtil.beanToMap(userVO);
        redisTemplate.opsForHash().putAll(tokenKey, userMap);
        redisTemplate.expire(tokenKey, USER_LOGIN_TTL, TimeUnit.DAYS);
        // 4 设置token响应前端
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(userVO, userLoginVO);
        userLoginVO.setToken(token);
        return userLoginVO;
    }

    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        UserVO currentUser = redissonManager.getUserByRequest(request);
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (user.getStatus().equals(UserStatusEnum.BAN.getValue())) {
            throw new BusinessException(ErrorCode.PROHIBITED, "账号已封禁");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        UserVO currentUser = redissonManager.getUserByRequest(request);
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        User user = this.getById(userId);
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    @Override
    public User isTourist(HttpServletRequest request) {
        UserVO currentUser = redissonManager.getUserByRequest(request);
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        UserVO currentUser = redissonManager.getUserByRequest(request);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        String tokenKey = redissonManager.getTokenKeyByRequest(request);
        redisTemplate.delete(tokenKey);
        return true;
    }

    @Override
    public void validUser(User user, boolean add) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();
        Long balance = user.getBalance();

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(userAccount, userPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            // 添加用户生成8位邀请码
            user.setInvitationCode(generateRandomString(8));
        }
        //  5. 账户不包含特殊字符
        // 匹配由数字、小写字母、大写字母组成的字符串,且字符串的长度至少为1个字符
        String pattern = "[0-9a-zA-Z]+";
        if (StringUtils.isNotBlank(userAccount) && !userAccount.matches(pattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号由数字、小写字母、大写字母组成");
        }
        if (ObjectUtils.isNotEmpty(balance) && balance < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "钱包余额不能为负数");
        }
        if (StringUtils.isNotBlank(userPassword)) {
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            user.setUserPassword(encryptPassword);
        }
        // 账户不能重复
        if (StringUtils.isNotBlank(userAccount)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
        }
    }

    @Override
    public UserVO updateVoucher(User loginUser) {
        String accessKey = DigestUtils.md5DigestAsHex((Arrays.toString(RandomUtil.randomBytes(10)) + SALT + VOUCHER).getBytes());
        String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + Arrays.toString(RandomUtil.randomBytes(10))).getBytes());
        loginUser.setAccessKey(accessKey);
        loginUser.setSecretKey(secretKey);
        boolean result = this.updateById(loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(loginUser, userVO);
        return userVO;
    }

    @Override
    public boolean reduceWalletBalance(Long userId, Long reduceScore) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.setSql("balance = balance - " + reduceScore);
        return this.update(userLambdaUpdateWrapper);
    }

    @Override
    public boolean increaseWalletBalance(Long userId, Long increaseScore) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.setSql("balance = balance + " + increaseScore);
        return this.update(userLambdaUpdateWrapper);
    }

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return
     */
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

}
