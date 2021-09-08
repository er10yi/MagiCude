# MongoDbUnauth|3F0000007E00000000000000D40700000400000061646D696E2E24636D640000000000FFFFFFFF18000000106C6973744461746162617365710100000000|中危||10|27017|mongodb|MongoDB|local|MongoDB未授权访问|
# -*- coding:utf-8 -*-
import binascii
import socket


def check(ip, domain, port, args, timeout, payload_map):
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        socket.setdefaulttimeout(int(timeout))
        s.connect((ip, int(port)))
        s.send(binascii.a2b_hex(args))
        byte_result = s.recv(1024)
        str_result = str(byte_result)
        if 'errmsg' not in str_result:
            return 'MongoDb未授权访问\n' + str_result
    except Exception:
        raise
