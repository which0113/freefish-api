package com.which.apicommon.model.dto;

import lombok.Data;

/**
 * @author which
 */
@Data
public class RequestParamsField {
    private String id;
    private String fieldName;
    private String type;
    private String desc;
    private String required;
}