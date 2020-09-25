#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi

echo "stopAll.sh将执行以下操作"
echo "kill nmap和masscan"
echo "停止docker中的容器：magicude_mysql magicude_redis magicude_rabbitmqnginxApp"
echo "停止center所有服务：eurekaapp.jar centerapp.jar agentapp.jar"
echo "如果部署了多个agent，请到agent服务器手动执行 stopAgent.sh"
echo -n "是否继续(10秒后默认N)? [y/N]: "
read -t 10 checkYes

if [[ $checkYes = "y" ]] ; then
    # kill jar
    jarNameArrays=("eurekaapp" "centerapp" "agentapp")
    for jarName in ${jarNameArrays[@]} ; do
        tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
        if [ $tempPid ] ;then
            echo "kill $jarName jar"
            kill -9 $tempPid
            echo "done."
        fi
    done
    # kill nmap masscan
    existFlag=`ps -ef|grep nmap|grep -v grep|cut -c 9-15`
    if [ $existFlag ] ;then
        echo "kill nmap"
        kill -9 $(pidof nmap)
        echo "done."
    fi
    existFlag=`ps -ef|grep masscan|grep -v grep|cut -c 9-15`
    if [ $existFlag ] ;then
        echo "kill masscan"
        kill -9 $(pidof masscan)
        echo "done."
    fi
    echo "docker stop container"
    temp=`docker stop magicude_mysql`
    temp=`docker stop magicude_redis`
    temp=`docker stop magicude_rabbitmq`
    temp=`docker stop nginxApp`
    echo "done."
else
    echo 
    exit 1  
fi