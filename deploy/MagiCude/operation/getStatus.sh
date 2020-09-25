#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi

jarNameArrays=("eurekaapp" "centerapp" "agentapp")
for jarName in ${jarNameArrays[@]} ; do
    tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
    if [ $tempPid ] ;then
        echo "$jarName jar status:"
        ps -ef|grep $jarName|grep -v grep
    else
        echo "!!!!!!!!!! magicude service $jarName not started. !!!!!!!!!!"
    fi
done