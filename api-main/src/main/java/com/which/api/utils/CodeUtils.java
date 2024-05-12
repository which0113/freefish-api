package com.which.api.utils;

import java.util.Random;

/**
 * 随机码生成工具
 *
 * @author which
 */
public class CodeUtils {

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return
     */
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
