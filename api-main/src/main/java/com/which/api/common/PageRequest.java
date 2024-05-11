package com.which.api.common;

import lombok.Data;

import static com.which.api.constant.CommonConstant.SORT_ORDER_DESC;
import static com.which.api.constant.CommonConstant.UPDATE_TIME;

/**
 * 分页请求
 *
 * @author which
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField = UPDATE_TIME;

    /**
     * 排序顺序
     */
    private String sortOrder = SORT_ORDER_DESC;
}
