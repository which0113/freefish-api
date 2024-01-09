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
     * 限流请求数量
     */
    Long RATE_LIMIT_NUM = 1L;

    /**
     * 等待时长
     */
    Long WAIT_TIME = 1000L;

}
