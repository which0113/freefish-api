package com.which.api.constant;

/**
 * 用户常量
 *
 * @author which
 */
public interface UserConstant {

    /**
     * 游客角色
     */
    String VISITOR_ROLE = "visitor";

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 演示角色
     */
    String DEMO_ROLE = "demo";

    /**
     * 盐值，混淆密码
     */
    String SALT = "which";

    /**
     * ak/sk 混淆
     */
    String VOUCHER = "accessKey_secretKey";

    /**
     * 预检请求
     */
    String OPTIONS = "OPTIONS";

}
