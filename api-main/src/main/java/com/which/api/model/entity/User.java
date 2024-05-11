package com.which.api.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author which
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 性别 0-男 1-女 2-保密
     */
    private String gender;
    /**
     * 用户角色：visitor / user / admin / demo
     */
    private String userRole;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * accessKey
     */
    private String accessKey;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * 钱包余额, 注册送30个币
     */
    private Long balance;
    /**
     * 邀请码
     */
    private String invitationCode;
    /**
     * 账号状态（0-正常 1-封号）
     */
    private Integer status;
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