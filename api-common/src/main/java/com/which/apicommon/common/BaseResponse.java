package com.which.apicommon.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author which
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -1693660536490703953L;
    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
