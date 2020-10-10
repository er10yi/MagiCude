#!/bin/bash
# 日志输出和临时环境变量
# source /root/.bash_profile下次登录才生效
# 如果第一次部署，一直不重连ssh，脚本执行完java和python3的命令就用不了

pythonVersionArrays=("3.9.0" "3.8.5")
openjdkVersion="15"
openjdkDirName="jdk-15"
openjdk="openjdk-15_linux-x64_bin.tar.gz"
nmapUrl="https://nmap.org/dist/nmap-7.90-1.x86_64.rpm"

export JAVA_HOME=/root/MagiCude/$openjdkDirName
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib:$CLASSPATH
export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH
export LD_LIBRARY_PATH=/usr/local/python3/lib

function logInfo(){
    echo -e "*****info*****\t $1"
}

function logWarn(){
    echo -e "\033[33m*****warn*****\t $1\033[0m"
}

function logError(){
    echo -e "\033[31m!!!!!error!!!!!\t $1\033[0m"
    exit 1
}

function logErrorNotExit(){
    echo -e "\033[31m!!!!!error!!!!!\t $1\033[0m"
}