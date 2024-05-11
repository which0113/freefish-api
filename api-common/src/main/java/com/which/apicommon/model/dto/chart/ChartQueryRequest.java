package com.which.apicommon.model.dto.chart;

import com.which.apicommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author which
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 生成的分析结论
     */
    private String genResult;
    /**
     * 图表状态
     */
    private String chartStatus;
    /**
     * 创建图表用户 id
     */
    private Long userId;
    /**
     * 创建用户
     */
    private String createUser;
}