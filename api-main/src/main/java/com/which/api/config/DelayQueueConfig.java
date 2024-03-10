package com.which.api.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

import static com.which.api.constant.MqConstant.*;

/**
 * 延迟队列配置
 *
 * @author which
 */
//@Configuration
public class DelayQueueConfig {

    @Bean
    public DirectExchange delayDirectExchange() {
        return new DirectExchange(API_DELAY_EXCHANGE);
    }

    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(API_DELAY_QUEUE)
                .ttl((int) API_TTL)
                .deadLetterExchange(API_DL_EXCHANGE)
                .deadLetterRoutingKey(API_DL_KEY)
                .build();
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(delayDirectExchange()).with(API_DELAY_KEY);
    }

}
