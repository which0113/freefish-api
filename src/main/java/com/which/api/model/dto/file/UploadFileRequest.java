package com.which.api.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author which
 */
@Data
public class UploadFileRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 业务
     */
    private String biz;
}