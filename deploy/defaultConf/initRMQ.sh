#!/bin/bash
# 初始化RabbitMQ
# @author 贰拾壹
# https://github.com/er10yi

echo "**********docker RabbitMQ初始化**********"
temp=`rabbitmqctl add_vhost /`
temp=`rabbitmqctl add_user magicude vpUNx2TpULV1kB7l`
temp=`rabbitmqctl set_user_tags magicude administrator`
temp=`rabbitmqctl set_permissions -p / magicude '.' '.' '.'`
echo "**********删除guest用户**********"
temp=`rabbitmqctl delete_user guest`
echo "**********创建队列**********"
temp=`rabbitmqctl eval 'rabbit_amqqueue:declare({resource, <<"/">>, queue, <<"scanresult">>}, true, false, [], none, "").'`
temp=`rabbitmqctl eval 'rabbit_amqqueue:declare({resource, <<"/">>, queue, <<"imresult">>}, true, false, [], none, "").'`
temp=`rabbitmqctl eval 'rabbit_amqqueue:declare({resource, <<"/">>, queue, <<"agentconfig">>}, true, false, [], none, "").'`