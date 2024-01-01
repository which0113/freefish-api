package com.which.api.model.dto.user;

import com.which.api.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 *
 * @author which
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 开放平台id
     */
    private String unionId;
    /**
     * 公众号openId
     */
    private String mpOpenId;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
}