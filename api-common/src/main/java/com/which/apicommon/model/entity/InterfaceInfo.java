package com.which.apicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 *
 * @author which
 * @TableName interface_info
 */
@TableName(value = "interface_info")
@Data
public class InterfaceInfo implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 接口名称
     */
    private String name;
    /**
     * 接口地址
     */
    private String url;
    /**
     * 发布人
     */
    private Long userId;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 接口请求参数
     */
    private String requestParams;
    /**
     * 接口响应参数
     */
    private String responseParams;
    /**
     * 扣除积分数
     */
    private Long reduceScore;
    /**
     * 请求示例
     */
    private String requestExample;
    /**
     * 请求头
     */
    private String requestHeader;
    /**
     * 响应头
     */
    private String responseHeader;
    /**
     * 返回格式(JSON等等)
     */
    private String returnFormat;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 接口状态（0-审核中 1-上线 2-下线）
     */
    private Integer status;
    /**
     * 接口总调用次数
     */
    private Long totalInvokes;
    /**
     * 接口头像
     */
    private String avatarUrl;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}