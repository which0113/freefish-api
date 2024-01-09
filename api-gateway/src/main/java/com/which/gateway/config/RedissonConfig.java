package com.which.gateway.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author which
 */
@Configuration
@PropertySource("classpath:application-dev.yml")
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    private int database;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String address = String.format("redis://%s:%s", host, port);
        config.useSingleServer()
                .setAddress(address)
                .setPassword(password)
                .setDatabase(database);
        // 2. 创建实例并返回
        return Redisson.create(config);
    }

}
