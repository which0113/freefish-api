# 数据库初始化
# @author which

-- 创建库
create database if not exists api_platform;

-- 切换库
use api_platform;

-- 用户表
create table if not exists user (
    id             bigint auto_increment comment 'id' primary key,
    userName       varchar(256)                           null comment '用户昵称',
    userAccount    varchar(256)                           not null comment '账号',
    userAvatar     varchar(1024)                          null comment '用户头像',
    email          varchar(256)                           null comment '邮箱',
    gender         varchar(10)  default '2'               null comment '性别 0-男 1-女 2-保密',
    userRole       varchar(256) default 'visitor'         not null comment '用户角色：visitor / user / admin / demo',
    userPassword   varchar(512)                           null comment '密码',
    accessKey      varchar(256)                           null comment 'accessKey',
    secretKey      varchar(256)                           null comment 'secretKey',
    balance        bigint       default 30                not null comment '钱包余额, 注册送30币',
    invitationCode varchar(256)                           null comment '邀请码',
    status         tinyint      default 0                 not null comment '账号状态（0-正常 1-封号）',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount unique (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 接口信息
create table if not exists interface_info (
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(256)                           not null comment '接口名称',
    url            varchar(256)                           not null comment '接口地址',
    userId         bigint                                 null comment '发布人',
    method         varchar(256)                           not null comment '请求方法',
    requestParams  text                                   null comment '接口请求参数',
    responseParams text                                   null comment '接口响应参数',
    reduceScore    bigint       default 0                 null comment '扣除积分数',
    requestExample text                                   null comment '请求示例',
    requestHeader  text                                   null comment '请求头',
    responseHeader text                                   null comment '响应头',
    returnFormat   varchar(512) default 'JSON'            null comment '返回格式(JSON等等)',
    description    varchar(256)                           null comment '描述信息',
    status         tinyint      default 0                 not null comment '接口状态（0-审核中 1-上线 2-下线）',
    totalInvokes   bigint       default 0                 not null comment '接口总调用次数',
    avatarUrl      varchar(1024)                          null comment '接口头像',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除'
) comment '接口信息' collate = utf8mb4_unicode_ci;

-- 用户接口调用表
create table if not exists user_interface_invoke (
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null comment '调用人id',
    interfaceId  bigint                             not null comment '接口id',
    totalInvokes bigint   default 0                 not null comment '总调用次数',
    status       tinyint  default 0                 not null comment '调用状态（0- 正常 1- 封号）',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
) comment '用户接口调用表' collate = utf8mb4_unicode_ci;

-- 图表信息表
create table if not exists chart (
    id          bigint auto_increment comment 'id' primary key,
    goal        text                                   null comment '分析目标',
    `name`      varchar(256)                           null comment '图表名称',
    chartData   text                                   null comment '图表数据',
    chartType   varchar(256)                           null comment '图表类型',
    genChart    text                                   null comment '生成的图表信息',
    genResult   text                                   null comment '生成的分析结论',
    chartStatus varchar(128) default 'wait'            not null comment 'wait-等待 running-生成中 succeed-成功生成 failed-生成失败',
    execMessage text                                   null comment '执行信息',
    userId      bigint                                 null comment '创建图表用户 id',
    createUser  varchar(256)                           not null comment '创建用户',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除'
) comment '图表信息表' collate = utf8mb4_unicode_ci;
