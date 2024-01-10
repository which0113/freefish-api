package com.which.api.interceptor;

import com.which.api.manager.RedissonManager;
import com.which.api.utils.UserThreadLocalUtils;
import com.which.apicommon.model.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

import static com.which.api.constant.RedisConstant.USER_LOGIN_TTL;
import static com.which.api.constant.UserConstant.OPTIONS;

/**
 * token刷新拦截器
 *
 * @author which
 */
@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

    @Resource
    private RedissonManager redissonManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (OPTIONS.equals(request.getMethod())) {
            return true;
        }
        String tokenKey = redissonManager.getTokenKeyByRequest(request);
        if (StringUtils.isBlank(tokenKey)) {
            return true;
        }
        UserVO userVO = redissonManager.getUserByTokenKey(tokenKey);
        if (userVO == null) {
            return true;
        }
        UserThreadLocalUtils.set(userVO);
        // todo USER_LOGIN_TTL: 2 * DAYS
        redisTemplate.expire(tokenKey, USER_LOGIN_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

}
