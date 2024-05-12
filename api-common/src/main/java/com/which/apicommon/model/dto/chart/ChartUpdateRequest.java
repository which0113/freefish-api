package com.which.apicommon.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @author which
 */
@Data
public class ChartUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 图表名称
     */
    private String name;
    /**
     * 分析目标
     */
    private String goal;
    /**
     * 图表信息
     */
    private String execMessage;
    /**
     * 图表类型
     */
    private String chartType;
}