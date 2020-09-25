#!/bin/bash
# @author 贰拾壹
# https://github.com/er10yi

# stop docker images
dockerNameArrays=("nginxApp" "magicude_mysql" "magicude_redis" "magicude_rabbitmq")
for imageName in ${dockerNameArrays[@]} ; do
    existFlag=`docker ps | grep $imageName |wc -L`
    if [ $existFlag -ne 0 ] ;then
        echo -n "docker stop "
        docker stop $imageName
        echo "done."
    fi
done