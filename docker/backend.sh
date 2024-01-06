# !/bin/bash
# 启动顺序严格为 main 在 gateway 之前（nacos注册服务需要时间）
java -jar ./jar/api-main-0.0.1.jar --spring.profiles.active=prod
java -jar ./jar/api-interface-0.0.1.jar --spring.profiles.active=prod
java -jar ./jar/api-gateway-0.0.1.jar --spring.profiles.active=prod
