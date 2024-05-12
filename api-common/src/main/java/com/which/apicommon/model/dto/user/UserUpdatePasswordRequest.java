package com.which.apicommon.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户修改密码
 *
 * @author which
 */
@Data
public class UserUpdatePasswordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户旧密码
     */
    private String userOldPassword;

    /**
     * 检查新密码
     */
    private String userNewPassword;

}