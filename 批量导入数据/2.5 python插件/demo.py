# # 单个python插件，# # 开头会被忽略
# 名称|参数|风险|辅助验证类型|超时|端口|服务|版本|漏洞关键字|漏洞名称|
# -*- coding:utf-8 -*-
import socket


def check(ip, domain, port, args, timeout, payload_map):
    username_list = payload_map.get('username')
    password_list = payload_map.get('password')

    try:
        # do something
        for username in username_list:
            for password in password_list:
                # do something with username and password
                return result
    except Exception:
        raise
