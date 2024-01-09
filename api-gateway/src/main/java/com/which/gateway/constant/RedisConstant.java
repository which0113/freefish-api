package com.which.gateway.constant;

/**
 * Redis常量
 *
 * @author which
 */
public interface RedisConstant {

    /**
     * 限流请求数量
     */
    Long RATE_LIMIT_NUM = 1L;

    /**
     * 等待时长
     */
    Long WAIT_TIME = 1000L;

    /**
     * 网关服务
     */
    String GATEWAY_SERVER_KEY = "gatewayServer:";

}
