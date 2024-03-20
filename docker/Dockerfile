# Docker 镜像构建
# @author which
FROM maven:3.8.1-jdk-8-slim as builder

WORKDIR /app

COPY jar ./jar
COPY backend.sh .

# 修改 backend.sh 文件 和 jar 文件夹的权限，使其可执行
RUN chmod -R +x ./jar
RUN chmod +x ./backend.sh

# 暴露端口
EXPOSE 9000 9001 9002

# 在容器中运行 backend.sh 脚本
CMD ["/bin/bash", "./backend.sh"]

# Deprecated
# 构建：docker build -t freefish-api:v0.0.1 .
# 运行：docker run -p 9000:9000 -p 9001:9001 -p 9002:9002 -d freefish-api:v0.0.1
