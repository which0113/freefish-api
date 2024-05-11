package com.which.apicommon.common;

import com.which.apicommon.constant.CommonConstant;
import lombok.Data;

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
    private String sortField = CommonConstant.UPDATE_TIME;

    /**
     * 排序顺序
     */
    private String sortOrder = CommonConstant.SORT_ORDER_DESC;
}
