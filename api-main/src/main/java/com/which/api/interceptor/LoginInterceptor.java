package com.which.api.interceptor;

import com.which.api.utils.UserThreadLocalUtils;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.which.api.constant.UserConstant.OPTIONS;

/**
 * 用户登录拦截器
 *
 * @author which
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (OPTIONS.equals(request.getMethod())) {
            return true;
        }
        if (UserThreadLocalUtils.get() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserThreadLocalUtils.remove();
    }

}
