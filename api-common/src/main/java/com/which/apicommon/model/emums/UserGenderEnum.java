package com.which.apicommon.model.emums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户性别枚举
 *
 * @author which
 */
public enum UserGenderEnum {

    /**
     * 男
     */
    male("男", "0"),

    /**
     * 女
     */
    female("女", "1"),

    /**
     * 秘密
     */
    secret("保密", "2");

    private final String text;

    private final String value;

    UserGenderEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static UserGenderEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (UserGenderEnum anEnum : UserGenderEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
