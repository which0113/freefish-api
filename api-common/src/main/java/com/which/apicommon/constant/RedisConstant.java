package com.which.apicommon.constant;

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
     * AI 分析积分扣除
     */
    String GEN_CHART_KEY = "ai:";

    /**
     * 用户的CURD
     */
    String USER_CURD_KEY = "userCRUD:";

    /**
     * 用户登录
     */
    String USER_LOGIN_KEY = "userLogin:token:";

    /**
     * 用户签到
     */
    String USER_CHECK_IN = "userCheckIn:";

    /**
     * 限流请求数量（每秒 3 次）
     */
    Long RATE_LIMIT_NUM = 3L;

    /**
     * 图表分析限流请求数量（每秒 1 次）
     */
    Long RATE_LIMIT_AI_NUM = 1L;

    /**
     * 获取分布式锁自旋时长
     */
    Long WAIT_TIME = 1000L;

    /**
     * 用户登录TTL（1天）
     */
    Long USER_LOGIN_TTL = 1L;

}
