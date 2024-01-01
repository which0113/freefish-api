# Docker 镜像构建
# @author which
FROM maven:3.8.1-jdk-8-slim as builder

WORKDIR /app

COPY freefish-api-0.0.1-SNAPSHOT.jar .

CMD ["java","-jar","freefish-api-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]

# 构建：docker build -t freefish-api:v0.0.1 .
# 运行：docker run -p 8088:8088 -d freefish-api:v0.0.1