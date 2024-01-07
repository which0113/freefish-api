package com.which.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author which
 */
@Configuration
public class GlobalWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名（如果Origins为*时，Credentials不能是true）
                .allowedOriginPatterns("*")
                //是否允许证书（cookie） 默认关闭
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                // 设置允许的header属性
                .allowedHeaders("*")
                //跨域允许时间
                .maxAge(3600);
    }

}
