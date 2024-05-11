package com.which.api.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 图表信息 VO
 *
 * @author which
 * @date 2024/05/11
 */
@Data
public class ChartVO {
    /**
     * ID
     */
    private Long id;
    /**
     * 分析目标
     */
    private String goal;
    /**
     * 图表名称
     */
    private String name;
    /**
     * 图表类型
     */
    private String chartType;
    /**
     * 生成的图表信息
     */
    private String genChart;
    /**
     * 生成的分析结论
     */
    private String genResult;
    /**
     * 图表状态
     */
    private String chartStatus;
    /**
     * 执行信息
     */
    private String execMessage;
    /**
     * 创建图表用户 ID
     */
    private Long userId;
    /**
     * 更新时间
     */
    private Date updateTime;
}
