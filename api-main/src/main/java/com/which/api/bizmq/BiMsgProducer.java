package com.which.api.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;

import static com.which.apicommon.constant.MqConstant.BI_DIRECT_EXCHANGE;
import static com.which.apicommon.constant.MqConstant.BI_ROUTING_KEY;

/**
 * @author which
 * RabbitMQ 生产者
 */
// @Component
public class BiMsgProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送信息
     *
     * @param chartId
     */
    public void sendMessage(Long chartId) {
        rabbitTemplate.convertAndSend(BI_DIRECT_EXCHANGE, BI_ROUTING_KEY, chartId);
    }

}