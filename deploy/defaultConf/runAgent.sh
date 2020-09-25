#!/bin/bash
# 启动agent
# @author 贰拾壹
# https://github.com/er10yi

tempPid=`ps -ef|grep agentapp|grep -v grep|cut -c 9-15`
if [ $tempPid ] ;then
    echo "agentapp已在运行，PID:$tempPid"
    echo "请执行 stopAgent.sh 后重试"
    exit 1
fi
echo "**********启动agentapp.jar**********"
nohup java -jar agentapp.jar --spring.config.location=agent.yml  > /dev/null 2>&1 &
sleep 5s
echo "********** ps -ef|grep agentapp|grep -v grep 查看运行状态**********"
ps -ef|grep agentapp|grep -v grep