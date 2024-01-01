package com.which.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.which.api.model.dto.user.UserRegisterRequest;
import com.which.api.model.entity.User;
import com.which.api.model.vo.UserVO;

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
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

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
     * @param add  是否为创建校验
     * @param user 接口信息
     */
    void validUser(User user, boolean add);

    /**
     * 更新用户凭证
     *
     * @param loginUser 登录用户
     * @return
     */
    UserVO updateVoucher(User loginUser);

}
