package com.which.api.config;

import com.which.api.interceptor.LoginInterceptor;
import com.which.api.interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author which
 */
@Configuration
public class GlobalWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名（如果Origins为*时，Credentials不能是true，所以用OriginPatterns）
                .allowedOriginPatterns("*")
                //是否允许证书（cookie） 默认关闭
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //设置允许的header属性
                .allowedHeaders("*")
                //跨域允许时间
                .maxAge(3600);
    }

    @Resource
    private LoginInterceptor loginInterceptor;

    @Resource
    private RefreshTokenInterceptor refreshTokenInterceptor;

    /**
     * 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // token刷新拦截器
        registry.addInterceptor(refreshTokenInterceptor)
                .addPathPatterns("/interfaceInfo/**")
                .addPathPatterns("/user/**")
                .addPathPatterns("/file/**")
                .order(0);
        // 登陆拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/interfaceInfo/**")
                .addPathPatterns("/user/**")
                .addPathPatterns("/file/**")
                .excludePathPatterns("/interfaceInfo/get")
                .excludePathPatterns("/interfaceInfo/searchText")
                .excludePathPatterns("/interfaceInfo/list/page")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register")
                .order(1);
    }

}
