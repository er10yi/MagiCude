#!/bin/bash
# 初始化环境
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

i=1
logInfo "复制initMySQL.sh到MySQL容器"
docker cp initMySQL.sh magicude_mysql:/usr/local/initMySQL.sh
while ( [ -z "`docker exec magicude_mysql tail -n 2 /usr/local/initMySQL.sh`" ] && [ $i -lt 6 ] )
do
    logInfo "复制initMySQL.sh到MySQL容器失败，重试第$i次"
    docker cp initMySQL.sh magicude_mysql:/usr/local/initMySQL.sh
    let "i++"
done
if [ $i -eq 6 ] ;then
    logError "复制initMySQL.sh到MySQL容器失败，重试第$i次失败，请重新执行 initCenterEnvironmentAndStart.sh"
else
    logInfo "复制initMySQL.sh到MySQL容器成功"
fi

i=1
logInfo "复制magicude.sql到MySQL容器"
docker cp db/magicude.sql magicude_mysql:/usr/local/magicude.sql
while ( [ -z "`docker exec magicude_mysql tail -n 2 /usr/local/magicude.sql`" ] && [ $i -lt 6 ] )
do
    logInfo "复制magicude.sql到MySQL容器失败，重试第$i次"
    docker start magicude_mysql
    sleep 10s
    docker cp db/magicude.sql magicude_mysql:/usr/local/magicude.sql
    let "i++"
done
if [ $i -eq 6 ] ;then
    logError "复制magicude.sql到MySQL容器失败，重试第$i次失败，请重新执行 initCenterEnvironmentAndStart.sh"
else
    logInfo "复制magicude.sql到MySQL容器成功"
fi

logInfo "MySQL容器执行initMySQL.sh"
# 增加initMySQL.sh执行成功的判断，magicude数据库存在，则initMySQL.sh执行成功
docker exec -it magicude_mysql /bin/bash -c "sh /usr/local/initMySQL.sh"
i=1
while ( [ -z "`docker exec -it magicude_mysql /bin/bash -c "mysql -uroot -p8TAQRc9EOkV607qm -e'CREATE DATABASE magicude DEFAULT CHARACTER SET utf8'" | grep "database exists"`" ] && [ $i -lt 6 ] )
do
    logInfo "MySQL容器initMySQL.sh执行失败，重试第$i次，重新复制initMySQL.sh"
    docker start magicude_mysql
    sleep 10s
    docker cp db/magicude.sql magicude_mysql:/usr/local/magicude.sql
    docker exec -it magicude_mysql /bin/bash -c "sh /usr/local/initMySQL.sh"
    let "i++"
done
if [ $i -eq 6 ] ;then
    logError "initMySQL.sh执行失败，重试第$i次失败，请重新执行 initCenterEnvironmentAndStart.sh"
else
    logInfo "MySQL容器initMySQL.sh执行成功"
fi

logInfo "删除MySQL容器初始化脚本"
docker exec -it magicude_mysql /bin/bash -c "rm -rf /usr/local/initMySQL.sh"
docker exec -it magicude_mysql /bin/bash -c "rm -rf /usr/local/magicude.sql"

logInfo "复制initRMQ.sh到RabbitMQ容器"
i=1
docker cp initRMQ.sh magicude_rabbitmq:/usr/local/initRMQ.sh
while ( [ -z "`docker exec magicude_rabbitmq tail -n 2 /usr/local/initRMQ.sh`" ] && [ $i -lt 6 ] )
do
    logInfo "复制initRMQ.sh到RabbitMQ容器失败，重试第$i次"
    docker start magicude_rabbitmq
    sleep 5s
    docker cp initRMQ.sh magicude_rabbitmq:/usr/local/initRMQ.sh
    let "i++"
done
if [ $i -eq 6 ] ;then
    logError "复制initRMQ.sh到RabbitMQ容器失败，重试第$i次失败，请重新执行 initCenterEnvironmentAndStart.sh"
else
    logInfo "复制initRMQ.sh到RabbitMQ容器成功"
fi

logInfo "RabbitMQ容器执行initRMQ.sh"
# 增加initRMQ.sh执行成功的判断，magicude用户存在，则initRMQ.sh执行成功
docker exec -it magicude_rabbitmq /bin/bash -c "sh /usr/local/initRMQ.sh"
i=1
while ( [ -z "`docker exec -it magicude_rabbitmq /bin/bash -c "rabbitmqctl add_user magicude vpUNx2TpULV1kB7l" | grep "already exists"`" ] && [ $i -lt 6 ] )
do
    logInfo "RabbitMQ容器initRMQ.sh执行失败，重试第$i次，重新复制initRMQ.sh"
    docker start magicude_rabbitmq
    sleep 10s
    docker cp initRMQ.sh magicude_rabbitmq:/usr/local/initRMQ.sh
    docker exec -it magicude_rabbitmq /bin/bash -c "sh /usr/local/initRMQ.sh"
    let "i++"
done
if [ $i -eq 6 ] ;then
    logError "initRMQ.sh执行失败，重试第$i次失败，请重新执行 initCenterEnvironmentAndStart.sh"
else
    logInfo "RabbitMQ容器initRMQ.sh执行成功"
fi

logInfo "删除RabbitMQ容器初始化脚本"
docker exec -it magicude_rabbitmq /bin/bash -c "rm -rf /usr/local/initRMQ.sh"

cd operation
sh runCenterService.sh

logInfo "执行magicude status"
.././magicude status