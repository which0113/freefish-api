package com.which.api.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @author which
 */
@Data
public class ChartAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分析目标
     */
    private String goal;
    /**
     * 图表信息
     */
    private String chartData;
    /**
     * 图表类型
     */
    private String chartType;
}