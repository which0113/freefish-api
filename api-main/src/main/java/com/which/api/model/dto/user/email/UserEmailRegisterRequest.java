package com.which.api.model.dto.user.email;

import lombok.Data;

import java.io.Serializable;

/**
 * @author which
 */
@Data
public class UserEmailRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String emailAccount;

    private String captcha;

    private String userName;

    private String invitationCode;

    private String agreeToAnAgreement;
}
