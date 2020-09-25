#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi

cd ..
echo "**********启动center服务**********"
echo "**********eurekaapp.jar**********"
nohup java -jar eurekaapp.jar --spring.config.location=eureka.yml  > /dev/null 2>&1 &
sleep 5s
echo "**********centerapp.jar**********"
nohup java -jar centerapp.jar --spring.config.location=center.yml  > /dev/null 2>&1 &
sleep 5s
echo "**********agentapp.jar**********"
nohup java -jar agentapp.jar --spring.config.location=agent.yml  > /dev/null 2>&1 &
sleep 50s
echo "**********查看运行状态**********"
sh operation/getStatus.sh