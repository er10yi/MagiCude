#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi

echo "restartAll.sh将执行以下操作"
echo "kill nmap和masscan"
echo "重启docker中的容器：magicude_mysql magicude_redis magicude_rabbitmq nginxApp"
echo "重启center所有服务：eurekaapp.jar centerapp.jar agentapp.jar"
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
    echo "docker restart container"
    temp=`docker restart magicude_mysql`
    temp=`docker restart magicude_redis`
    temp=`docker restart magicude_rabbitmq`
    setenforce 0
    temp=`docker restart nginxApp`
    echo "done."
    cd ..
    echo "**********启动center服务**********"
    nohup java -jar eurekaapp.jar --spring.config.location=eureka.yml  > /dev/null 2>&1 &
    nohup java -jar centerapp.jar --spring.config.location=center.yml  > /dev/null 2>&1 &
    nohup java -jar agentapp.jar --spring.config.location=agent.yml  > /dev/null 2>&1 &
    sleep 20s
    echo "********** 查看运行状态**********"
    sh operation/getStatus.sh
else
    echo 
    exit 1  
fi