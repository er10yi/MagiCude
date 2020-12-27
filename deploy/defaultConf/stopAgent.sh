#!/bin/bash
# 停止agent
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

logWarn "停止 nmap和masscan"
existFlag=`ps -ef|grep nmap|grep -v grep| wc -L`
if [ $existFlag -ne 0 ] ;then
    kill -9 $(pidof nmap)
fi
existFlag=` ps -ef|grep masscan|grep -v grep| wc -L`
if [ $existFlag -ne 0 ] ;then
    kill -9 $(pidof masscan)
fi
logInfo "完成"
logWarn "停止 agentapp.jar"
# kill agentapp
tempPid=`ps -ef|grep agentapp|grep -v grep|cut -c 9-15`
if [ $tempPid ] ;then
    kill -9 $tempPid
fi
logInfo "完成"