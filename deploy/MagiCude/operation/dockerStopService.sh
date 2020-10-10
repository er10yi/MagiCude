#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

logWarn "停止docker容器"
# stop docker images
dockerNameArrays=("nginxApp" "magicude_mysql" "magicude_redis" "magicude_rabbitmq")
for imageName in ${dockerNameArrays[@]} ; do
    existFlag=`docker ps | grep $imageName |wc -L`
    if [ $existFlag -ne 0 ] ;then
        logWarn "正在停止 $imageName"
        docker stop $imageName > /dev/null 2>&1 &
        wait
    fi
done
logInfo "完成"