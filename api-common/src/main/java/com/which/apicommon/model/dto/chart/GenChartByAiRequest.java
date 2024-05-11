package com.which.apicommon.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author which
 */
@Data
public class GenChartByAiRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

}