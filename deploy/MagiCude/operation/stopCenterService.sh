#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

logWarn "停止 MagiCude 服务"
jarNameArrays=("eurekaapp" "centerapp" "agentapp")
for jarName in ${jarNameArrays[@]} ; do
    tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
    if [ $tempPid ] ;then
        logWarn "停止 $jarName"
        kill -9 $tempPid
    fi
done
logInfo "完成"