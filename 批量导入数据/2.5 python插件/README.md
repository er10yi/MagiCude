## python插件说明

py文件使用```# # ```开头注释

模板可以参考demo.python或已有插件

单个python插件，# # 开头会被忽略

### 格式说明
除```# # ```注释外，第一行是配置信息，第二行之后是代码，如：
```
# 名称|参数|风险|辅助验证类型|超时|端口|服务|版本|漏洞关键字|漏洞名称|（这里有个换行）
插件代码
```
1. 风险: 信息, 低危, 中危, 高危, 严重, 致命
2. 辅助验证类型: http,dns，可以两个同时设置，漏洞没有回显，但可以执行命令，如curl，nslookup等
3. 端口，插件对应的端口，可以多个value，英文,逗号分割，资产/目标端口与此相同，才会调用该插件
4. 服务，插件对应端口的服务，可以多个value，英文,逗号分割，资产/目标端口的服务包含此服务，则调用该插件
5. 版本，插件对应端口服务的版本，可以多个value，英文,逗号分割，资产/目标端口的版本包含此版本，则调用该插件
6. 漏洞关键字，有回显的漏洞，必须配置漏洞关键词，否则系统无法判断目标是否存在漏洞，结果中包含该关键词，则存在漏洞；结果包含header和response，可以多个value，英文,逗号分割
7. 漏洞名称，插件对应漏洞wiki中的漏洞，没有则填默认，单个漏洞

模块导入说明，模块只能import或者from开头，否则未安装的模块不会自动安装
```
如：import socket
或：from pexpect import pxssh
```
python插件未安装的模块，会自动从以下源获取安装，最后一次会尝试默认源
```
https://pypi.douban.com/simple/
https://pypi.mirrors.ustc.edu.cn/simple/
https://pypi.tuna.tsinghua.edu.cn/simple/
https://pypi.hustunique.com/
https://mirrors.aliyun.com/pypi/simple/
```

### 弱口令类插件说明
python弱口令插件命名规范

**名称必须包含WeakPass如SSHWeakPass**

此时python代码才能获取到传入的用户名密码字典
```
username_list = payload_map.get('username')
password_list = payload_map.get('password')
```

### 辅助验证说明
1. 插件需要http辅助验证：辅助验证为http，代码中使用以下方式获取web接口（http://centerip:9001/center/pluginchecker）
    ```
    http_validate_list = payload_map.get('http_validate')
    http_validate = http_validate_list[0]
    ```
2. 插件需要dns辅助验证：辅助验证为dns，代码中使用以下方式获取dns解析的随机域名和dns服务器ip地址
    ```
    dns_validate_list = payload_map.get('dns_validate')
    dns_validate = dns_validate_list[0]
    ```
3. http辅助验证和dns辅助验证可以同时设置

### 代码说明

代码格式可以参考已有python的插件，函数为
```
def check(ip, domain, port, args, timeout, payload_map)
```
1. ip，域名
    * ip没有域名的情况下，domain为空
    * ip有域名，则可以对ip和域名进行处理
2. 函数参数说明
    * ip、domain、port由配置的端口、服务、版本确定，不需要手动指定
    * args插件的参数，也可以作为其它自定义数据的传入
    * timeout为配置信息的超时，也可以作为其它自定义数据的传入
    * payload_map为传递的用户名密码字典、辅助验证的http接口或随机域名和dns的ip
    
3. 函数需要直接返回结果，如果配置信息的漏洞关键词，则系统会新增漏洞，不需要在python代码中判断。需要本地调试成功后才能新增，保持代码格式（如缩进，换行等），否则会执行失败
4. 函数有异常直接raise

### demo代码
```
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
```

### 其他

* ip没有域名
  
    任务中不包含域名，此时check方法仅能根据ip进行处理

* ip有域名

    ip有域名或多个域名，每个任务都包含单个ip和单个域名，此时check方法可以同时处理ip和域名

