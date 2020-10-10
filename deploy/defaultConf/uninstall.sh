#!/bin/bash
# 魔方-MagiCude卸载脚本
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

logInfo "$0将执行以下操作"
logWarn "1.停止魔方-MagiCude所有服务"
logWarn "2.停止nmap和masscan"
logWarn "3.停止并删除docker容器"
logWarn "4.还原系统源"
logWarn "5.移除依赖"
logWarn "6.移除nmap和masscan"
logWarn "7.移除Python3"
logWarn "8.恢复环境变量"
logWarn "9.删除/root/MagiCude"
logErrorNotExit "如果部署了多个agent，请到agent服务器手动执行 $0"
logErrorNotExit "魔方-MagiCude所有数据将丢失"
echo -n "是否继续(10秒后默认N)? [y/N]: "
read -t 10 checkYes
if [[ $checkYes != "y" ]] ; then
    echo
    logInfo "退出卸载" 
    exit 1
fi

logWarn "停止MagiCude所有服务"
jarNameArrays=("eurekaapp" "centerapp" "agentapp")
for jarName in ${jarNameArrays[@]} ; do
    tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
    if [ $tempPid ] ;then
        kill -9 $tempPid
    fi
done
logWarn "停止nmap和masscan"
existFlag=`ps -ef|grep nmap|grep -v grep|cut -c 9-15`
if [ $existFlag ] ;then
    kill -9 $(pidof nmap)
fi
existFlag=`ps -ef|grep masscan|grep -v grep|cut -c 9-15`
if [ $existFlag ] ;then
    kill -9 $(pidof masscan)
fi
logWarn "停止并删除docker容器"
type docker >/dev/null 2>&1
if [ $? -eq 0 ] ;then
    dockerNameArrays=("nginxApp" "magicude_mysql" "magicude_redis" "magicude_rabbitmq")
    systemctl restart docker
    for imageName in ${dockerNameArrays[@]} ; do
        existFlag=`docker ps -a | grep $imageName |wc -L`
        if [ $existFlag -ne 0 ] ;then
            docker stop $imageName >/dev/null 2>&1
            docker rm $imageName >/dev/null 2>&1
        fi
    done
    systemctl stop docker
fi

# 还原系统源
existFlag=`cat /etc/yum.repos.d/CentOS-Base.repo | grep ustc |wc -L`
if [ $existFlag -ne 0 ] ;then
    logWarn "还原系统源"
    # rm -rf /etc/yum.repos.d/CentOS-Base.repo
    mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup.uninstall
    mv /etc/yum.repos.d/CentOS-Base.repo.backup /etc/yum.repos.d/CentOS-Base.repo
    yum clean all >/dev/null 2>&1
    yum makecache >/dev/null 2>&1
fi

logWarn "移除依赖"
yum -y remove wget fontconfig stix-fonts ntpdate gcc make libpcap libpcap-dev clang git >/dev/null 2>&1
yum -y remove zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gdbm-devel db4-devel libpcap-devel xz-devel libffi-devel zlib1g-dev zlib* >/dev/null 2>&1
# docker
yum -y remove docker* >/dev/null 2>&1
rm -rf /etc/docker >/dev/null 2>&1
rm -rf /run/docker >/dev/null 2>&1
rm -rf /var/lib/dockershim >/dev/null 2>&1
rm -rf /var/lib/docker >/dev/null 2>&1

logWarn "移除nmap和masscan"
yum -y remove nmap >/dev/null 2>&1
rm -rf /usr/bin/masscan >/dev/null 2>&1

logWarn "移除Python3"
rm -rf /usr/local/python3 >/dev/null 2>&1
rm -rf /usr/local/bin/python3 >/dev/null 2>&1
rm -rf /usr/local/bin/pip3 >/dev/null 2>&1

logWarn "恢复环境变量"
sed -i "/export JAVA_HOME=\/root\/MagiCude\/$openjdkDirName/d" /root/.bash_profile
sed -i "/export JRE_HOME=\$JAVA_HOME\/jre/d" /root/.bash_profile
sed -i "/export CLASSPATH=\$JAVA_HOME\/lib:\$JRE_HOME\/lib:\$CLASSPATH/d" /root/.bash_profile
sed -i "/export PATH=\$JAVA_HOME\/bin:\$JRE_HOME\/bin:\$PATH/d" /root/.bash_profile
sed -i "/export LD_LIBRARY_PATH=\/usr\/local\/python3\/lib/d" /root/.bash_profile
source /root/.bash_profile

echo -n "是否删除/root/MagiCude(10秒后默认N)? [y/N]: "
read -t 10 checkYes
if [[ $checkYes == "y" ]] ; then
    rm -rf /root/MagiCude
else
    echo
    logInfo "/root/MagiCude未删除，可重新登录远程连接后再次部署"
    logInfo "如不再使用，可手动删除"
fi