package com.which.api.bizmq;

import com.rabbitmq.client.Channel;
import com.which.api.model.entity.User;
import com.which.api.model.enums.UserRoleEnum;
import com.which.api.service.UserService;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.which.api.constant.MqConstant.*;

/**
 * RabbitMQ 消费者
 *
 * @author which
 */
@Slf4j
@Component
public class ApiMsgConsumer {

    @Resource
    private UserService userService;

    /**
     * 注销注册3天后未登录的用户账号
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = API_DL_QUEUE, durable = "true"),
            exchange = @Exchange(value = API_DL_EXCHANGE),
            key = API_DL_KEY
    ))
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "MQ消息为空");
        }
        Long userId = Long.parseLong(message);
        User user = userService.getById(userId);
        String userRole = user.getUserRole();
        if (StringUtils.isBlank(userRole)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户角色为空");
        }
        // 如果用户角色不为游客，代表已登录激活账号
        if (!userRole.equals(UserRoleEnum.VISITOR.getValue())) {
            channel.basicAck(deliveryTag, false);
            log.info("用户账号激活成功，userId：{}", userId);
            return;
        }
        boolean remove = userService.removeById(userId);
        if (!remove) {
            // 消息处理失败
            channel.basicNack(deliveryTag, false, false);
            log.error("注销用户账号失败，userId：{}", userId);
            return;
        }
        // 消息处理成功
        channel.basicAck(deliveryTag, false);
        log.info("注销用户账号成功，userId：{}", userId);
    }

}
