#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

logWarn "$0 将执行以下操作"
logWarn "停止nmap和masscan"
logWarn "重启docker中的容器：magicude_mysql magicude_redis magicude_rabbitmq nginxApp"
logWarn "重启center所有服务：eurekaapp.jar centerapp.jar agentapp.jar"
echo -n "是否继续(10秒后默认N)? [y/N]: "
read -t 10 checkYes
if [[ $checkYes = "y" ]] ; then
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
    logWarn "docker重启容器"
    docker restart magicude_mysql > /dev/null 2>&1 &
    docker restart magicude_redis > /dev/null 2>&1 &
    docker restart magicude_rabbitmq > /dev/null 2>&1 &
    setenforce 0
    docker restart nginxApp > /dev/null 2>&1 &
    logInfo "完成"
    cd ..
    logInfo "启动center服务"
    nohup java -jar eurekaapp.jar --spring.config.location=eureka.yml  > /dev/null 2>&1 &
    nohup java -jar centerapp.jar --spring.config.location=center.yml  > /dev/null 2>&1 &
    nohup java -jar agentapp.jar --spring.config.location=agent.yml  > /dev/null 2>&1 &
    sleep 20s
    logInfo "查看运行状态"
    cd operation
    sh getStatus.sh
else
    echo 
    exit 1  
fi