#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi

jarNameArrays=("eurekaapp" "centerapp" "agentapp")
for jarName in ${jarNameArrays[@]} ; do
    tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
    if [ $tempPid ] ;then
        echo "kill $jarName jar"
        kill -9 $tempPid
        echo "done."
    fi
done