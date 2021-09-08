# 魔方-MagiCude

## slogan

赋予数据无限可能

## 起名来源

三阶魔方的变化有`(8!*3^8*12!*2^12)/(3*2*2)=43,252,003,274,489,856,000` 约等于4.3*10^19，即4325亿亿种变化，寓意数据也有无限种使用方式，与slogan**赋予数据无限可能**相呼应。

## 项目说明

魔方-MagiCude，基于Spring Boot微服务架构的系统；具有分布式端口（漏洞）扫描、资产安全管理、实时威胁监控与通知、自动漏洞闭环、漏洞wiki、邮件报告通知、poc框架等功能。

1. 手动资产管理：应用系统-域名-ip-端口-负责人-标签等信息关联
2. 自动资产管理：ip、端口(协议、状态、服务、版本)、域内DNS反解析域名、web页面及链接抓取
3. 高危资产及漏洞实时推送：钉钉群机器人、企微群机器人实时推送
4. 资产报告和漏洞报告定时推送：邮件汇总报告到默认邮箱、项目负责人只收到自己相关的报告，可配置定时周期
5. 资产、漏洞统计：折线图、饼图展示相关信息
6. 漏洞wiki：漏洞原理和修复方案等相关信息，需要持续维护和管理
7. 基于Python3的自定义插件：方便应急响应
8. 标签分类：分类可任意添加，可对ip、端口、应用系统手动加标签
9. 扫描任务管理：实时任务状态，便捷的任务操作
10. 白名单机制：项目组端口白名单、ip端口扫描白名单、提醒白名单、页面标题白名单、链接域名白名单

这个项目能解决什么[需求和痛点](https://github.com/er10yi/MagiCude/wiki/Best-Practice#%E9%9C%80%E6%B1%82%E5%8F%8A%E7%97%9B%E7%82%B9 )

## 简述

center：数据库、redis、MQ、[管理前端](https://github.com/er10yi/MagiCude-admin )、任务控制和分发；任务进redis，并由MQ发送。

agent：端口扫描任务、插件任务、http页面抓取任务执行；从redis中取任务，结果通过MQ返回。

端口扫描基于nmap和masscan；主机发现基于nmap ping扫描；安全扫描基于nse脚本和自定义插件，自定义插件包括Java、Python3、基于HTTP/HTTPS访问的json插件，无回显命令执行可使用DNS或HTTP方式辅助确认；web页面抓取基于自定义爬虫。

实时推送：钉钉群机器人、企微群机器人。

## 安装指南
### 可尝试最新版的预览版3
[魔方-MagiCude2.2 Preview3](https://github.com/er10yi/MagiCude/releases/tag/v2.2-preview3)，**预览版与当前正式版有较大区别，readme和wiki中部分文档已过时。**


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

![公众号二维码](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE1/qrcode_for_Septemberend.jpg)

## 部分功能截图

### 首页

![首页](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/1%E9%A6%96%E9%A1%B5.png)

### 统计

资产统计1

![资产统计1](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/3%E8%B5%84%E4%BA%A7%E7%BB%9F%E8%AE%A1.png)

资产统计2

![资产统计2](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/3%E8%B5%84%E4%BA%A7%E7%BB%9F%E8%AE%A12.png)

高危统计

![高危](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/4%E9%AB%98%E5%8D%B1.png)

### 任务

![任务](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/2%E4%BB%BB%E5%8A%A1.png)

### 资产

资产ip所有信息

![ip所有信息](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE1/%E8%B5%84%E4%BA%A7ip.png)

资产端口

![资产端口](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE1/%E8%B5%84%E4%BA%A7%E7%AB%AF%E5%8F%A3.png)

### 检测结果、提醒日志

web信息

![webinfo](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/7_1webinfo.png)

web信息和url

![webinfo和url](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/7_webinfo%E5%92%8Curl.png)

检测结果和提醒日志

![检测结果和提醒日志](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/8%E6%A3%80%E6%B5%8B%E7%BB%93%E6%9E%9C%E5%92%8C%E6%8F%90%E9%86%92%E6%97%A5%E5%BF%97.png)

### 邮件报告及钉钉群推送

邮件所有资产报告（默认提醒邮箱），项目负责人只会收到属于自己项目的资产报告

![邮件资产报告](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/%E9%82%AE%E4%BB%B6%E8%B5%84%E4%BA%A7%E6%8A%A5%E5%91%8A.png)

邮件所有漏洞报告（默认提醒邮箱），项目负责人只会收到属于自己项目的漏洞报告

![邮件漏洞报告](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/%E9%82%AE%E4%BB%B6%E6%BC%8F%E6%B4%9E%E6%8A%A5%E5%91%8A.png)

钉钉群推送

电脑客户端

![钉钉群推送2](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/%E9%92%89%E9%92%89%E7%BE%A4%E6%8E%A8%E9%80%812.png)

手机端

![钉钉群推送1](https://github.com/er10yi/MagiCude/raw/master-old/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E%E5%9B%BE/%E9%92%89%E9%92%89%E7%BE%A4%E6%8E%A8%E9%80%811.png)

