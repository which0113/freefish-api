package com.which.api.bizmq;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
class ApiMsgProducerTest {

    @Resource
    private ApiMsgProducer apiMsgProducer;

    @Test
    void sendMessage() {
        apiMsgProducer.sendMessage(666L);
        log.info("消息发送成功");
    }

}