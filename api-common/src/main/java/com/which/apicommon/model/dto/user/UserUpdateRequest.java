package com.which.apicommon.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author which
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 性别
     */
    private String gender;
    /**
     * 用户角色
     */
    private String userRole;
    /**
     * 账号状态
     */
    private Integer status;
    /**
     * 密码
     */
    private String userPassword;

    /**
     * 钱包余额（分）
     */
    private Long balance;
}