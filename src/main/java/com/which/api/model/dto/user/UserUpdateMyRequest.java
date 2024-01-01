package com.which.api.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新个人信息请求
 *
 * @author which
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 简介
     */
    private String userProfile;
}