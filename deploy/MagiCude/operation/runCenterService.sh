#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

cd ..
logInfo "启动 MagiCude 服务"
logInfo "eurekaapp.jar"
nohup java -jar eurekaapp.jar --spring.config.location=eureka.yml  > /dev/null 2>&1 &
sleep 5s
logInfo "centerapp.jar"
nohup java -jar centerapp.jar --spring.config.location=center.yml  > /dev/null 2>&1 &
sleep 5s
logInfo "agentapp.jar"
nohup java -jar agentapp.jar --spring.config.location=agent.yml  > /dev/null 2>&1 &
sleep 50s
logInfo "查看 MagiCude 服务运行状态"
sh operation/getStatus.sh