#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

jarNameArrays=("eurekaapp" "centerapp" "agentapp")
for jarName in ${jarNameArrays[@]} ; do
    tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
    if [ $tempPid ] ;then
        logInfo "$jarName 状态：`ps -ef|grep $jarName|grep -v grep`"
    else
        logErrorNotExit "MagiCude 服务 $jarName 未启动"
    fi
done