#!/bin/bash
# 停止agent
# @author 贰拾壹
# https://github.com/er10yi

echo "**********停止agentapp.jar**********"
# kill agentapp
tempPid=`ps -ef|grep agentapp|grep -v grep|cut -c 9-15`
if [ $tempPid ] ;then
    kill -9 $tempPid
fi
echo "done."