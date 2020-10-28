#!/bin/bash
# 初始化RabbitMQ
# @author 贰拾壹
# https://github.com/er10yi

echo  "*****info*****   docker RabbitMQ初始化"
rabbitmqctl add_vhost / >/dev/null 2>&1
rabbitmqctl add_user magicude vpUNx2TpULV1kB7l >/dev/null 2>&1
rabbitmqctl set_user_tags magicude administrator >/dev/null 2>&1
rabbitmqctl set_permissions -p / magicude '.' '.' '.' >/dev/null 2>&1
echo  "*****info*****   删除guest用户"
rabbitmqctl delete_user guest >/dev/null 2>&1
echo  "*****info*****   创建队列"
rabbitmqctl eval 'rabbit_amqqueue:declare({resource, <<"/">>, queue, <<"scanresult">>}, true, false, [], none, "").' >/dev/null 2>&1
rabbitmqctl eval 'rabbit_amqqueue:declare({resource, <<"/">>, queue, <<"imresult">>}, true, false, [], none, "").' >/dev/null 2>&1
rabbitmqctl eval 'rabbit_amqqueue:declare({resource, <<"/">>, queue, <<"agentconfig">>}, true, false, [], none, "").' >/dev/null 2>&1