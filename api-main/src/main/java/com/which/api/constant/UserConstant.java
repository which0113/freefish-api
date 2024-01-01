package com.which.api.constant;

/**
 * 用户常量
 *
 * @author which
 */
public interface UserConstant {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 盐值，混淆密码
     */
    String SALT = "which";

    /**
     * ak/sk 混淆
     */
    String VOUCHER = "accessKey_secretKey";

}
