package com.which.apicommon.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author which
 */
@Data
public class BiVO {

    private static final long serialVersionUID = 1;

    private String genChat;

    private String genResult;

    private Long chartId;

    private LocalDateTime updateTime;

}
