package com.which.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.which.apicommon.model.dto.user.UserLoginRequest;
import com.which.apicommon.model.dto.user.UserRegisterRequest;
import com.which.apicommon.model.dto.user.UserUpdatePasswordRequest;
import com.which.apicommon.model.entity.User;
import com.which.apicommon.model.vo.UserLoginVO;
import com.which.apicommon.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author which
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 校验参数
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户签到
     *
     * @param request
     * @return
     */
    boolean userCheckIn(HttpServletRequest request);

    /**
     * 用户登录
     *
     * @param request
     * @param userLoginRequest 用户账户
     * @return 脱敏后的用户信息
     */
    UserLoginVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    UserVO getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为演示账号
     *
     * @param request
     * @return
     */
    boolean isDemo(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 是游客
     *
     * @param request
     * @return
     */
    User isTourist(HttpServletRequest request);

    /**
     * 校验
     *
     * @param user 接口信息
     */
    void validUser(User user);

    /**
     * 更新密码
     *
     * @param userUpdatePasswordRequest 用户更新密码请求
     * @param request                   请求
     * @return boolean
     */
    boolean updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest, HttpServletRequest request);

    /**
     * 更新用户凭证
     *
     * @param loginUser 登录用户
     * @return
     */
    UserVO updateVoucher(User loginUser);

    /**
     * 减少钱包余额
     *
     * @param userId      用户id
     * @param reduceScore
     * @return boolean
     */
    boolean reduceWalletBalance(Long userId, Long reduceScore);

    /**
     * 增加钱包余额
     *
     * @param userId        用户id
     * @param increaseScore
     * @return boolean
     */
    boolean increaseWalletBalance(Long userId, Long increaseScore);

}
