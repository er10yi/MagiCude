#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

logInfo "检查docker容器状态"
setenforce 0
dockerNameArrays=("nginxApp" "magicude_mysql" "magicude_redis" "magicude_rabbitmq")
for imageName in ${dockerNameArrays[@]} ; do
    existFlag=`docker ps | grep $imageName |wc -L`
    if [ $existFlag -eq 0 ] ;then
        logWarn "$imageName 未启动，正在启动 $imageName"
        docker start $imageName >/dev/null 2>&1
        logInfo "$imageName 完成启动"
    else logInfo "$imageName 已启动"
    fi
done