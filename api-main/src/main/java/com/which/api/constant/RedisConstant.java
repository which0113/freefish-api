package com.which.api.constant;

/**
 * Redis常量
 *
 * @author which
 */
public interface RedisConstant {

    /**
     * 限流
     */
    String RATE_LIMIT_KEY = "rateLimit:";

    /**
     * 用户的CURD
     */
    String USER_CURD_KEY = "userCRUD:";

    /**
     * 用户登录
     */
    String USER_LOGIN_KEY = "userLogin:token:";

    /**
     * 限流请求数量（每秒 3 次）
     */
    Long RATE_LIMIT_NUM = 3L;

    /**
     * 获取分布式锁自旋时长
     */
    Long WAIT_TIME = 1000L;

    /**
     * 用户登录TTL（1天）
     */
    Long USER_LOGIN_TTL = 1L;

}
