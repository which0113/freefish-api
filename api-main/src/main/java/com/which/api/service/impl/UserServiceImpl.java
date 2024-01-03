package com.which.api.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.which.api.common.ErrorCode;
import com.which.api.exception.BusinessException;
import com.which.api.mapper.UserMapper;
import com.which.api.model.dto.user.UserRegisterRequest;
import com.which.api.model.entity.User;
import com.which.api.model.enums.UserStatusEnum;
import com.which.api.model.vo.UserVO;
import com.which.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Random;

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
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        User invitationCodeUser = null;
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
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短,不能小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短,不能低于8位字符");
        }
        //  5. 账户不包含特殊字符
        // 匹配由数字、小写字母、大写字母组成的字符串,且字符串的长度至少为1个字符
        String pattern = "[0-9a-zA-Z]+";
        if (!userAccount.matches(pattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号需由数字、小写字母、大写字母组成");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (user.getStatus().equals(UserStatusEnum.BAN.getValue())) {
            throw new BusinessException(ErrorCode.PROHIBITED, "账号已封禁");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
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
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
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
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
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
