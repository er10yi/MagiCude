# 魔方-MagiCude

by [贰拾壹](https://github.com/er10yi )

赋予数据无限可能：分布式端口扫描、资产安全管理、实时威胁监控与通知、高效漏洞闭环、漏洞wiki、邮件报告通知、poc框架，and more!

## 起名来源

三阶魔方的变化有```(8!*3^8*12!*2^12)/(3*2*2)=43,252,003,274,489,856,000``` 约等于4.3*10^19，即4325亿亿种变化，寓意数据也有无限种使用方式。

随着企业发展规模变大，企业拥有越来越多的服务器和虚拟服务器，安全人员每天耗费大量的精力在杂乱无章的资产上，但得到的结果是微乎其微，投入与产出不成正比。如何高效的获取服务器IP、端口信息、web信息、url链接、漏洞等资产信息，并对其进行有效的安全管理，如何建立漏洞wiki并自动闭环漏洞，减少安全部门与业务部门的沟通，如何及时发送资产信息和处理结果给负责人进行整改并自动确认整改完成，如何有效获取DHCP的办公机IP与用户关联，成为难题。

魔方-MagiCude给出了一个解决方案。

## 说明

1. **本文档为快速部署说明，快速上手见[《快速上手指南》](https://github.com/er10yi/MagiCude/blob/master/%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B%E6%8C%87%E5%8D%97.md )，分布式部署及详细使用说明文档见[《使用说明》](https://github.com/er10yi/MagiCude/blob/master/%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md )，开发说明见[《开发说明》](https://github.com/er10yi/MagiCude/blob/master/%E5%BC%80%E5%8F%91%E8%AF%B4%E6%98%8E.md )**

2. 如果部署在生产环境，请参考使用说明配置防火墙

3. 前端项目见 [MagiCude-admin](https://github.com/er10yi/MagiCude-admin)

## 要求

1. 所有内容编码均为utf-8

2. 服务器系统要求 CentOS 7.x

假设服务器已装好，服务器ip地址为```192.168.12.128```

## 三步快速部署

### 1.生成配置文件

下载部署包（Releases页面）解压后，deploy目录下双击```GenPass.bat```（Windows下，*uinx/mac直接执行```GenPass.bat```里面的内容就行了），将在当前目录生成newPass.txt，新的配置文件和初始化脚本会自动复制到MagiCude目录下

### 2.将deploy目录下的MagiCude上传到服务器root目录下

### 3.初始化环境并运行系统

终端执行```sh initCenterEnvironmentAndStart.sh```，初始化环境和数据并启动系统

**需要手动选择center的ip地址**

![选择ip](./使用说明图1/选择ip.png)

安装结束时，如果没有错误信息，则成功安装

## 其他

### 系统操作

```MagiCude```目录下```magicude```为可执行文件，用法

```shell
./magicude start # 启动魔方
./magicude stop # 停止魔方
./magicude restart # 重启魔方
./magicude status # 查看魔方运行状态
```


### 登录魔方-MagiCude

访问服务器ip，使用admin（后台管理员）密码登录，当前是http://192.168.12.128/

```
账号：MagiCude
密码：见newPass.txt
```

## 微信讨论组

如有任何问题，欢迎群里反馈

扫描下方二维码，关注公众号，回复**自己的微信号+魔方**，即刻拥有！

![qrcode_for_Septemberend](./使用说明图1/qrcode_for_Septemberend.jpg)
