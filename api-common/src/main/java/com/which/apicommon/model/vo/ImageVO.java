package com.which.apicommon.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author which
 */
@Data
public class ImageVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String uid;
    private String name;
    private String status;
    private String url;
    private LocalDateTime updateTime;
}