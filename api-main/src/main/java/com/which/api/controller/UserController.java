package com.which.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.which.api.annotation.AuthCheck;
import com.which.api.common.DeleteRequest;
import com.which.api.common.IdRequest;
import com.which.api.model.dto.user.*;
import com.which.api.model.dto.user.email.UserBindEmailRequest;
import com.which.api.model.dto.user.email.UserEmailLoginRequest;
import com.which.api.model.dto.user.email.UserEmailRegisterRequest;
import com.which.api.model.dto.user.email.UserUnBindEmailRequest;
import com.which.api.model.entity.User;
import com.which.api.model.enums.UserStatusEnum;
import com.which.api.model.vo.UserLoginVO;
import com.which.api.service.UserService;
import com.which.apicommon.common.BaseResponse;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import com.which.apicommon.common.ResultUtils;
import com.which.apicommon.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.which.api.constant.UserConstant.ADMIN_ROLE;

/**
 * 用户接口
 *
 * @author which
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserLoginVO user = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        UserVO user = userService.getLoginUser(request);
        return ResultUtils.success(user);
    }

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 校验
        userService.validUser(user, true);


        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (ObjectUtils.anyNull(deleteRequest, deleteRequest.getId()) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.removeById(deleteRequest.getId()));
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<UserVO> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                           HttpServletRequest request) {
        if (ObjectUtils.anyNull(userUpdateRequest, userUpdateRequest.getId()) || userUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 管理员才能操作
        boolean adminOperation = ObjectUtils.isNotEmpty(userUpdateRequest.getBalance())
                || StringUtils.isNoneBlank(userUpdateRequest.getUserRole())
                || StringUtils.isNoneBlank(userUpdateRequest.getUserPassword());
        // 校验是否登录
        UserVO loginUser = userService.getLoginUser(request);
        // 处理管理员业务,不是管理员抛异常
        if (adminOperation && !loginUser.getUserRole().equals(ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (!loginUser.getUserRole().equals(ADMIN_ROLE) && !userUpdateRequest.getId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只有本人或管理员可以修改");
        }

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        // 参数校验
        userService.validUser(user, false);

        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, user.getId());

        boolean result = userService.update(user, userLambdaUpdateWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userService.getById(user.getId()), userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> getUserVOById(UserQueryRequest userQueryRequest) {
        if (null == userQueryRequest) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User userQuery = new User();
        BeanUtils.copyProperties(userQueryRequest, userQuery);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest) {
        User userQuery = new User();
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        BeanUtils.copyProperties(userQueryRequest, userQuery);

        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String gender = userQueryRequest.getGender();
        String userRole = userQueryRequest.getUserRole();
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName)
                .eq(StringUtils.isNotBlank(userAccount), "userAccount", userAccount)
                .eq(StringUtils.isNotBlank(gender), "gender", gender)
                .eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        Page<User> userPage = userService.page(new Page<>(current, pageSize), queryWrapper);
        Page<UserVO> userVoPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVoPage.setRecords(userVOList);
        return ResultUtils.success(userVoPage);
    }

    /**
     * 更新用户凭证
     *
     * @param request
     * @return
     */
    @PostMapping("/update/voucher")
    public BaseResponse<UserVO> updateVoucher(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(loginUser, user);
        UserVO userVO = userService.updateVoucher(user);
        return ResultUtils.success(userVO);
    }

    /**
     * 解封
     *
     * @param idRequest id请求
     * @return
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/normal")
    public BaseResponse<Boolean> normalUser(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        user.setStatus(UserStatusEnum.NORMAL.getValue());
        return ResultUtils.success(userService.updateById(user));
    }

    /**
     * 封号
     *
     * @param idRequest id请求
     * @return
     */
    @PostMapping("/ban")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> banUser(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        user.setStatus(UserStatusEnum.BAN.getValue());
        return ResultUtils.success(userService.updateById(user));
    }

    /**
     * 用户电子邮件登录
     *
     * @param userEmailLoginRequest 用户登录请求
     * @param request               请求
     * @return
     */
    @PostMapping("/email/login")
    public BaseResponse<UserVO> userEmailLogin(@RequestBody UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "开发中！");
    }

    /**
     * 用户绑定电子邮件
     *
     * @param request              请求
     * @param userBindEmailRequest 用户绑定电子邮件请求
     * @return
     */
    @PostMapping("/bind/login")
    public BaseResponse<UserVO> userBindEmail(@RequestBody UserBindEmailRequest userBindEmailRequest, HttpServletRequest request) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "开发中！");
    }

    /**
     * 用户取消绑定电子邮件
     *
     * @param request                请求
     * @param userUnBindEmailRequest 用户取消绑定电子邮件请求
     * @return
     */
    @PostMapping("/unbindEmail")
    public BaseResponse<UserVO> userUnBindEmail(@RequestBody UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "开发中！");
    }

    /**
     * 用户电子邮件注册
     *
     * @param userEmailRegisterRequest 用户电子邮件注册请求
     * @return
     */
    @PostMapping("/email/register")
    public BaseResponse<Long> userEmailRegister(@RequestBody UserEmailRegisterRequest userEmailRegisterRequest) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "开发中！");
    }

    /**
     * 获取验证码
     *
     * @param emailAccount 电子邮件账号
     * @return
     */
    @GetMapping("/getCaptcha")
    public BaseResponse<Boolean> getCaptcha(String emailAccount) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "开发中！");
    }

}
