# MySQLWeakPass|mysql|严重||10|3306|mysql|mysql|mysql|MySQL弱密码/未授权访问|
# -*- coding:utf-8 -*-
import pymysql


def check(ip, domain, port, args, timeout, payload_map):
    username_list = payload_map.get('username')
    password_list = payload_map.get('password')

    try:
        for username in username_list:
            for password in password_list:
                try:
                    conn = pymysql.connect(host=ip, port=int(
                        port), user=username, passwd=password, db=args, charset='utf8')
                    result = conn.db
                    conn.close()
                    return '用户名密码: ' + username + ':' + password + '\n' + result.decode()
                except Exception as e:
                    pass
    except Exception:
        raise
