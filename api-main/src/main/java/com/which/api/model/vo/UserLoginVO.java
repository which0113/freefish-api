package com.which.api.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图token（脱敏）
 *
 * @author which
 */
@Data
public class UserLoginVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户昵称
     */
    private String email;
    /**
     * 邀请码
     */
    private String invitationCode;
    /**
     * 账号状态（0- 正常 1- 封号）
     */
    private Integer status;
    /**
     * 钱包余额（分）
     */
    private Long balance;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 访问密钥
     */
    private String accessKey;
    /**
     * 秘密密钥
     */
    private String secretKey;
    /**
     * 性别
     */
    private String gender;
    /**
     * 用户角色
     */
    private String userRole;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * token
     */
    private String token;
}