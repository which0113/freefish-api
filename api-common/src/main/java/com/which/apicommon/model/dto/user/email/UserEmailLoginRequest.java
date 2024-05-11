package com.which.apicommon.model.dto.user.email;

import lombok.Data;

import java.io.Serializable;


/**
 * @author which
 */
@Data
public class UserEmailLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String emailAccount;

    private String captcha;
}
