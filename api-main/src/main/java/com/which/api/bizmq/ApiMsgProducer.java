package com.which.api.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;

import static com.which.apicommon.constant.MqConstant.API_DELAY_EXCHANGE;
import static com.which.apicommon.constant.MqConstant.API_DELAY_KEY;

/**
 * RabbitMQ 生产者
 *
 * @author which
 */
//@Component
public class ApiMsgProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送信息
     *
     * @param userId
     */
    public void sendMessage(Long userId) {
        rabbitTemplate.convertAndSend(API_DELAY_EXCHANGE, API_DELAY_KEY, userId);
    }

}