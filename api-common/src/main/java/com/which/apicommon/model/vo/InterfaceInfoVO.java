package com.which.apicommon.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息 VO
 *
 * @author which
 * @date 2024/05/11
 */
@Data
public class InterfaceInfoVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;

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
     * 请求方法
     */
    private String method;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 扣除积分数
     */
    private Long reduceScore;

    /**
     * 发布人
     */
    private Long userId;

    /**
     * 更新时间
     */
    private Date updateTime;
}
