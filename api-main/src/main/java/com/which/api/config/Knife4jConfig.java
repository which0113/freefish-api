package com.which.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j 接口文档配置
 *
 * @author which
 */
@Configuration
@EnableSwagger2
public class Knife4jConfig {

    @Bean
    public Docket openApi3() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("咸鱼API")
                        .description("咸鱼API接口文档")
                        .version("1.0")
                        .contact(new Contact(
                                "which",
                                "https://github.com/which0113",
                                "1782180242@qq.com"))
                        .build())
                .select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.which.api.controller"))
                .paths(PathSelectors.any())
                .paths(PathSelectors.regex("/.*/error").negate())
                .build();
    }

}