# Docker 镜像构建
# @author which
FROM maven:3.8.1-jdk-8-slim as builder

WORKDIR /app

#COPY api-main-0.0.1.jar .
#COPY api-interface-0.0.1.jar .
COPY api-gateway-0.0.1.jar .

#CMD ["java","-jar","api-main-0.0.1.jar","--spring.profiles.active=prod"]
#CMD ["java","-jar","api-interface-0.0.1.jar","--spring.profiles.active=prod"]
CMD ["java","-jar","api-gateway-0.0.1.jar","--spring.profiles.active=prod"]

# 构建：docker build -t api-main:v0.0.1 .
# 运行：docker run -p 9001:9001 -d api-main:v0.0.1