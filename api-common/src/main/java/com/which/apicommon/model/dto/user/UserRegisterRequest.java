package com.which.apicommon.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author which
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;

    private String userName;

    private String checkPassword;

    private String invitationCode;

    private String agreeToAnAgreement;
}
