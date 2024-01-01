# 数据库初始化
# @author which

-- 创建库
create database if not exists api_platform;

-- 切换库
use api_platform;

-- 用户表
create table if not exists user (
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 接口信息
create table if not exists interface_info (
    id             bigint                             not null auto_increment comment '主键' primary key,
    `name`         varchar(256)                       not null comment '用户名',
    `description`  varchar(256)                       null comment '描述',
    url            varchar(512)                       not null comment '接口地址',
    requestHeader  text                               null comment '请求头',
    responseHeader text                               null comment '响应头',
    userId         varchar(256)                       not null comment '创建人',
    `status`       int      default 0                 not null comment '接口状态（0 - 关闭， 1 - 开启））',
    method         varchar(256)                       not null comment '请求类型',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '接口信息表';