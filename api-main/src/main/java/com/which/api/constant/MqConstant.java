package com.which.api.constant;

import java.util.concurrent.TimeUnit;

/**
 * RabbitMQ 常量
 *
 * @author which
 */
public interface MqConstant {

    // 延迟队列

    String API_DELAY_QUEUE = "api.delay.queue";

    String API_DELAY_EXCHANGE = "api.delay.direct";

    String API_DELAY_KEY = "api.delay.key";

    long API_TTL = TimeUnit.DAYS.toMillis(3);

    // 死信队列

    String API_DL_QUEUE = "api.dl.queue";

    String API_DL_EXCHANGE = "api.dl.direct";

    String API_DL_KEY = "api.dl.key";

}
