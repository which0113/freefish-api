package com.which.apicommon.model.emums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户状态枚举
 *
 * @author which
 */
public enum UserStatusEnum {

    /**
     * 正常
     */
    NORMAL("正常", 0),
    /**
     * 封号
     */
    BAN("封禁", 1);

    private final String text;

    private final Integer value;

    UserStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static UserStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (UserStatusEnum anEnum : UserStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
