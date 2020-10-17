# 魔方-MagiCude

## slogan

赋予数据无限可能

## 起名来源

三阶魔方的变化有`(8!*3^8*12!*2^12)/(3*2*2)=43,252,003,274,489,856,000` 约等于4.3*10^19，即4325亿亿种变化，寓意数据也有无限种使用方式，与slogan**赋予数据无限可能**相呼应。

## 项目说明

魔方-MagiCude，基于Spring Boot微服务架构的系统；具有分布式端口（漏洞）扫描、资产安全管理、实时威胁监控与通知、自动漏洞闭环、漏洞wiki、邮件报告通知、poc框架等功能。

通过魔方-MagiCude可高效获取服务器IP，端口信息，web信息，url链接，漏洞等资产信息，合理的定时扫描任务可伪实时监控端口状态及漏洞状态，并能够对资产进行有效的安全管理；合理建立漏洞wiki，能减少安全部门与业务部门之间的沟通；配置钉钉群机器人后能够即时漏洞提醒，配置邮箱信息后能够定时发送资产报告和漏洞报告；扫描任务会结自动闭环漏洞；通过DNS解析有效获取DHCP的办公机IP与用户关联；具备丰富的图表统计及POC框架功能。

## 简述

center：数据库、redis、MQ、[管理前端](https://github.com/er10yi/MagiCude-admin )、任务控制和分发；任务进redis，并由MQ发送。

agent：端口扫描任务、插件任务、http页面抓取任务执行；从redis中取任务，结果通过MQ返回。

端口扫描基于nmap和masscan；主机发现基于nmap ping扫描；安全扫描基于nse脚本和自定义插件，自定义插件包括Java、Python3、基于HTTP/HTTPS访问的json插件，无回显命令执行可使用DNS或HTTP方式辅助确认；web页面抓取基于自定义爬虫。

## 安装指南

### 要求说明

1. 所有内容编码均为utf-8
2. 服务器系统要求 CentOS 7.x
3. **生产环境使用，请在部署完成后参考wiki中的[防火墙配置](https://github.com/er10yi/MagiCude/wiki/Firewall-Config)进行防火墙配置**

### 生成配置文件

1. [Releases页面](https://github.com/er10yi/MagiCude/releases)下载最新部署包。
2. 解压后，[deploy目录](https://github.com/er10yi/MagiCude/wiki/UserPass-Table-Deploy-Structure )下双击`GenPass.bat`（Windows下，*uinx/Mac直接执行`GenPass.bat`里面的内容），将在当前目录生成**newPass.txt（请注意备份）**，新的配置文件和初始化脚本会自动复制到**MagiCude（后面部署都是用这个目录）**目录下。

### center和agent部署在同一台服务器

1. 将上述提到的**MagiCude**上传到服务器```root```目录下。

2. 服务器上执行`sh initCenterEnvironmentAndStart.sh`，初始化环境和数据并启动系统，按提示**手动选择center的ip地址**即可。

3. 安装结束时，如果没有错误信息，则成功安装，稍等几分钟[前端登录](https://github.com/er10yi/MagiCude/blob/master/README.md#%E5%89%8D%E7%AB%AF%E7%99%BB%E5%BD%95)即可。

### agent分布式部署

1. 需要先执行上述步骤[center和agent部署在同一台服务器](https://github.com/er10yi/MagiCude/blob/master/README.md#center%E5%92%8Cagent%E9%83%A8%E7%BD%B2%E5%9C%A8%E5%90%8C%E4%B8%80%E5%8F%B0%E6%9C%8D%E5%8A%A1%E5%99%A8)部署center，完成之后。

2. agent服务器的```root```目录下新建```MagiCude```目录。

3. 将上述提到的**MagiCude**目录的以下文件上传到agent服务器```MagiCude```目录

   ```sh
   agent.yml
   agentapp.jar
   initAgentEnvironmentAndStart.sh
   runAgent.sh
   stopAgent.sh
   uninstall.sh
   util.sh
   ```

4. agent服务器上执行``` sh initAgentEnvironmentAndStart.sh```，执行初始化agent环境并启动agent，按提示输入**agent的名字和center的ip**即可。

5. 安装结束时，如果没有错误信息，则成功安装，等待agent注册即可。

## 使用说明

### 前端登录

访问服务器ip登录

```
账号：MagiCude
密码：见newPass.txt（admin那行，用户名是MagiCude，不是admin）
```

![登录页面](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE1/%E7%99%BB%E5%BD%95%E9%A1%B5%E9%9D%A2.png)

### 快速上手指南

[快速上手指南](https://github.com/er10yi/MagiCude/wiki/Getting-Started-Guide )，快速了解系统的基本功能。

### 最佳实践

[最佳实践](https://github.com/er10yi/MagiCude/wiki/Best-Practice )，了解系统后，合理使用系统功能并发挥系统最大价值。

### 说明文档

[说明文档](https://github.com/er10yi/MagiCude/wiki/User-Guide )，详细说明文档。

### 开发说明

[开发说明](https://github.com/er10yi/MagiCude/wiki/Develop-Guide )，简要的开发说明。

### 更多

请查看[wiki](https://github.com/er10yi/MagiCude/wiki )中的相关内容。

## 微信讨论组

如有任何问题，欢迎群里反馈。

扫描下方二维码，关注公众号，回复**自己的微信号+魔方**，即刻拥有！

![公众号二维码](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE1/qrcode_for_Septemberend.jpg)

## 部分功能截图

### 首页

![首页](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/1%E9%A6%96%E9%A1%B5.png)

### 统计

资产统计1

![资产统计1](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/3%E8%B5%84%E4%BA%A7%E7%BB%9F%E8%AE%A1.png)

资产统计2

![资产统计2](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/3%E8%B5%84%E4%BA%A7%E7%BB%9F%E8%AE%A12.png)

高危统计

![高危](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/4%E9%AB%98%E5%8D%B1.png)

### 任务

![任务](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/2%E4%BB%BB%E5%8A%A1.png)

### 资产

资产ip所有信息

![ip所有信息](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE1/%E8%B5%84%E4%BA%A7ip.png)

资产端口

![资产端口](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE1/%E8%B5%84%E4%BA%A7%E7%AB%AF%E5%8F%A3.png)

### 检测结果、提醒日志

web信息

![webinfo](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/7_1webinfo.png)

web信息和url

![webinfo和url](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/7_webinfo%E5%92%8Curl.png)

检测结果和提醒日志

![检测结果和提醒日志](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/8%E6%A3%80%E6%B5%8B%E7%BB%93%E6%9E%9C%E5%92%8C%E6%8F%90%E9%86%92%E6%97%A5%E5%BF%97.png)

### 邮件报告及钉钉群推送

邮件所有资产报告（默认提醒邮箱），项目负责人只会收到属于自己项目的资产报告

![邮件资产报告](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/%E9%82%AE%E4%BB%B6%E8%B5%84%E4%BA%A7%E6%8A%A5%E5%91%8A.png)

邮件所有漏洞报告（默认提醒邮箱），项目负责人只会收到属于自己项目的漏洞报告

![邮件漏洞报告](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/%E9%82%AE%E4%BB%B6%E6%BC%8F%E6%B4%9E%E6%8A%A5%E5%91%8A.png)

钉钉群推送

电脑客户端

![钉钉群推送2](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/%E9%92%89%E9%92%89%E7%BE%A4%E6%8E%A8%E9%80%812.png)

手机端

![钉钉群推送1](https://github.com/er10yi/MagiCude/raw/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/%E9%92%89%E9%92%89%E7%BE%A4%E6%8E%A8%E9%80%811.png)

