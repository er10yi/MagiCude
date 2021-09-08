# MemcachedUnauth|stats|中危||3000|11211|memcached|Memcached|uptime|Memcached未授权访问|
# -*- coding:utf-8 -*-
import socket


def check(ip, domain, port, args, timeout, payload_map):
    try:
        socket.setdefaulttimeout(int(timeout))
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((ip, int(port)))
        s.send((args + '\r\n').encode())
        byte_result = s.recv(1024)
        return 'memcached未授权访问\n' + byte_result.decode()
    except Exception:
        raise
