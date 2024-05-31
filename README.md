<p align="center">
    <img src=https://img.freefish.love/logo.png width=188/>
</p>
<h1 align="center">咸鱼-API 接口开放平台</h1>
<p align="center"><strong>咸鱼-API 接口开放平台是一个为用户和开发者提供全面API接口调用服务的平台 🛠</strong></p>
<div align="center">
<a target="_blank" href="https://github.com/which0113/api-backend">
    <img alt="" src="https://github.com/which0113/api-backend/badge/star.svg?theme=gvp"/>
</a>
    <img alt="Maven" src="https://raster.shields.io/badge/Maven-3.8.1-red.svg"/>
<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
        <img alt="" src="https://img.shields.io/badge/JDK-1.8+-green.svg"/>
</a>
    <img alt="SpringBoot" src="https://raster.shields.io/badge/SpringBoot-2.7+-green.svg"/>
</div>

## 项目介绍

😀 作为用户您可以通过注册登录账户，获取接口调用权限，并根据自己的需求浏览和下载适合的接口。您可以在线进行接口调试，快速验证接口的功能和效果。

💻 作为开发者，我们提供了[咸鱼-API SDK](https://github.com/which0113/api-sdk)，
通过[开发者凭证](https://www.freefish.love/account/center)即可将轻松集成接口到您的项目中，实现更高效的开发和调用。

🤝 您可以将自己的接口接入到 **_咸鱼-API_** 接口开放平台平台上，并发布给其他用户使用。
您可以管理和各个接口，以便更好地分析和优化接口性能。

🔎 您只需要导入最原始的数据集，输入需要分析的目标，就能利用[AI智能生成](https://www.freefish.love/analyse)
一个可下载的数据分析图表和分析结论。

🏁 无论您是用户还是开发者，**_咸鱼-API_**
咸鱼-API 接口开放平台都致力于提供稳定、安全、高效的接口调用和数据分析服务，帮助您实现更高效、便捷化的开发和调用体验。

## 网站导航

- [项目在线演示地址 🔗](https://www.freefish.love)
- 演示账号：demo
- 密码：12345678
- [项目前端地址 🔗](https://github.com/which0113/api-frontend)
- [项目后端地址 🔗](https://github.com/which0113/freefish-api)

## 使用指导

### 克隆项目到本地

```bash
git clone git@github.com:which0113/freefish-api.git
```

### 运行后端

#### 注意事项

- JDK 版本为 1.8
- MySQL 版本为 8.0+
- Maven 版本为 3.9+
- Redis 版本为 5.0+
- RabbitMQ 版本为 3.9+
- Nacos 版本为 2.3+

#### 其他

- [application-dev.yml](api-main%2Fsrc%2Fmain%2Fresources%2Fapplication-dev.yml) 配置文件可修改服务启动端口、MySQL、Redis
  配置等等
- [ddl.sql](sql%2Fddl.sql) 文件是 MySQL 数据库文件，可  **Ctrl A 全选 + Ctrl Enter 执行** 快速初始化数据库

#### 运行

准备项目启动所需的服务

```
MySQL 数据库

Redis 缓存中间件

RabbitMQ 消息队列

Nacos 注册中心
```

分别修改 api-gateway 和 api-main 模块的 application-dev.yml 配置文件

```
# 必须修改，否则不能运行 oss 服务
# 对象存储 oss，不会请参考官方文档
alibaba:
  cloud:
    oss:
      endpoint: your_oss_serve_url
    access-key: xxx
    secret-key: xxx
# 必须修改至少一个，否则不能运行 AI 服务
# AI 服务，不会请参考官方文档
# 鱼聪明AI
yuapi:
  client:
    access-key: xxx
    secret-key: xxx
# OpenAI
open-ai-client:
  api-key: "sk-xxx"
  api-host: https://代理地址
```

启动项目的3个子服务

```
api-gateway 网关服务

api-interface 接口服务

api-main 核心服务
```

## 项目展示

### 首页

![home.png](doc%2Fhome.png)

### 接口广场

#### 接口展示

![api-display.png](doc%2Fapi-display.png)

#### 接口详情

![api-details.png](doc%2Fapi-details.png)

##### API文档

![api-doc.png](doc%2Fapi-doc.png)

##### 错误码

![error-codes.png](doc%2Ferror-codes.png)

##### 示例代码

![sample-code.png](doc%2Fsample-code.png)

##### 在线调试

![online-commissioning.png](doc%2Fonline-commissioning.png)

### 智能分析

![intelligent-analytics.png](doc%2Fintelligent-analytics.png)

### 我的图表

#### 图表展示

![chart-display.png](doc%2Fchart-display.png)

#### 图表下载

![chart-download.png](doc%2Fchart-download.png)

### 用户功能

#### 登录

![login.png](doc%2Flogin.png)

#### 注册

![register.png](doc%2Fregister.png)

#### 个人中心

![personal-center.png](doc%2Fpersonal-center.png)

### 管理员功能

#### 接口管理

![interface-management.png](doc%2Finterface-management.png)

#### 图表管理

![chart-management.png](doc%2Fchart-management.png)

#### 用户管理

![user-management.png](doc%2Fuser-management.png)

## 技术栈

- Spring Boot 2.7+
- Spring MVC
- MySQL 数据库
- WebSocket 异步通知
- EasyExcel 处理工具
- Dubbo 分布式（RPC、Nacos）
- RabbitMQ 消息队列
- Spring Cloud Gateway 微服务网关
- API 签名认证（Http 调用）
- Spring Boot Starter（SDK 开发）
- Swagger + Knife4j 接口文档
- ThreadLocal Redis（Token 权限校验和续签）
- Redisson 分布式限流
- Apache Commons Lang3 工具类
- MyBatis-Plus 及 MyBatis X 自动生成
- Hutool、Apache Common Utils、Gson 等工具库