package com.which.api.model.dto.email;

import lombok.Data;

import java.io.Serializable;

/**
 * @author which
 */
@Data
public class UserUnBindEmailRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String emailAccount;

    private String captcha;
}
