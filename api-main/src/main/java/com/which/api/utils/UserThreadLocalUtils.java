package com.which.api.utils;

import com.which.apicommon.model.vo.UserVO;

/**
 * @author which
 */
public class UserThreadLocalUtils {

    private static final ThreadLocal<UserVO> USER_THREAD = new ThreadLocal<>();

    public static void set(UserVO user) {
        USER_THREAD.set(user);
    }

    public static UserVO get() {
        return USER_THREAD.get();
    }

    /**
     * 防止内存泄漏
     */
    public static void remove() {
        USER_THREAD.remove();
    }

}