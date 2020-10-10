#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

logWarn "$0 将执行以下操作"
logWarn "停止nmap和masscan"
logWarn "停止docker中的容器：magicude_mysql magicude_redis magicude_rabbitmqnginxApp"
logWarn "停止center所有服务：eurekaapp.jar centerapp.jar agentapp.jar"
logWarn "如果部署了多个agent，请到agent服务器手动执行 stopAgent.sh"
echo -n "是否继续(10秒后默认N)? [y/N]: "
read -t 10 checkYes

if [[ $checkYes = "y" ]] ; then
    logWarn "停止 MagiCude 所有服务"
    # kill jar
    jarNameArrays=("eurekaapp" "centerapp" "agentapp")
    for jarName in ${jarNameArrays[@]} ; do
        tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
        if [ $tempPid ] ;then
            logWarn "停止 $jarName"
            kill -9 $tempPid
        fi
    done
    logInfo "完成"
    logWarn "停止 nmap和masscan"
    # kill nmap masscan
    existFlag=`ps -ef|grep nmap|grep -v grep|cut -c 9-15`
    if [ $existFlag ] ;then
        logWarn "停止 nmap"
        kill -9 $(pidof nmap)
    fi
    existFlag=`ps -ef|grep masscan|grep -v grep|cut -c 9-15`
    if [ $existFlag ] ;then
        logWarn "停止 masscan"
        kill -9 $(pidof masscan)
    fi
    logInfo "完成"
    logWarn "docker停止容器"
    docker stop magicude_mysql > /dev/null 2>&1 &
    docker stop magicude_redis > /dev/null 2>&1 &
    docker stop magicude_rabbitmq > /dev/null 2>&1 &
    docker stop nginxApp > /dev/null 2>&1 &
    logInfo "完成"
else
    echo 
    exit 1  
fi