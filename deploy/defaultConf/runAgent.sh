#!/bin/bash
# 启动agent
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

tempPid=`ps -ef|grep agentapp|grep -v grep|cut -c 9-15`
if [ $tempPid ] ;then
    logErrorNotExit "agentapp已在运行，PID:$tempPid"
    logError "请执行 stopAgent.sh 后重试"
    exit 1
fi
logInfo "启动agentapp.jar"
nohup java -jar agentapp.jar --spring.config.location=agent.yml  > /dev/null 2>&1 &
sleep 5s
logInfo "状态：`ps -ef|grep agentapp|grep -v grep`"
logInfo "完成"