# FTPWeakPass|10|中危||10|21|ftp|ftp|FTPWeakPass|FTP弱密码/未授权访问|
# -*- coding:utf-8 -*-
import ftplib


def check(ip, domain, port, args, timeout, payload_map):
    username_list = payload_map.get('username')
    password_list = payload_map.get('password')

    try:
        ftp = ftplib.FTP()
        ftp.timeout = int(timeout)
        for username in username_list:
            for password in password_list:
                try:
                    ftp.connect(ip, int(port))
                    ftp.login(username, password)
                    if username == 'ftp':
                        result = "FTP允许匿名访问"
                    else:
                        result = "用户名密码: " + username + ":" + password
                    return result
                except ftplib.error_perm:
                    pass
        ftp.quit()
    except Exception:
        raise
