package com.which.apicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图表信息表
 *
 * @author which
 * @TableName chart
 */
@TableName(value = "chart")
@Data
public class Chart implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 图表信息
     */
    private String chartData;
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
     * 图表状态 wait-等待 running-生成中 succeed-成功生成 failed-生成失败
     */
    private String chartStatus;
    /**
     * 执行信息
     */
    private String execMessage;
    /**
     * 创建图表用户 id
     */
    private Long userId;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}