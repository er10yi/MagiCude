# RedisWeakPass|info|高危||3000|6379|redis|Redis key-value store|redis_version|Redis弱密码/未授权访问|
# -*- coding:utf-8 -*-
import socket


def check(ip, domain, port, args, timeout, payload_map):
    password_list = payload_map.get('password')

    try:
        socket.setdefaulttimeout(int(timeout))
        for password in password_list:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect((ip, int(port)))
            s.send(('AUTH ' + password + '\r\n' + args + '\r\n').encode())
            byte_result = s.recv(1024)
            str_result = str(byte_result)
            if 'no password is set' in str_result:
                return 'Redis未设置密码' + byte_result.decode()
            if '+OK' in str_result:
                return '密码：' + password + '\n' + byte_result.decode()
    except Exception:
        raise
