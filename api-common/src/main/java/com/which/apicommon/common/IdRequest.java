package com.which.apicommon.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author which
 */
@Data
public class IdRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
}