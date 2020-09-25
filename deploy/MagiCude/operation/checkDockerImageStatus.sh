#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi


echo "检查docker容器状态"
echo "check docker image status"
setenforce 0
dockerNameArrays=("nginxApp" "magicude_mysql" "magicude_redis" "magicude_rabbitmq")
for imageName in ${dockerNameArrays[@]} ; do
    existFlag=`docker ps | grep $imageName |wc -L`
    if [ $existFlag -eq 0 ] ;then
        echo "docker $imageName not started, starting $imageName now"
        temp=`docker start $imageName`
        echo "docker start $imageName done. Status:"
        docker ps | grep $imageName
    else echo "docker $imageName already started."
    fi
done