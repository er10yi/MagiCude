#!/bin/bash
# 初始化环境
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

echo 
echo "魔方-MagiCude agent部署脚本 V2.6"
echo "@author 贰拾壹"
echo "https://github.com/er10yi"
echo

# 增加操作系统判断
if [ ! -f /etc/redhat-release ];then
    logError "$0 仅支持CentOS 7.x"
fi
tempVersion=`cat /etc/redhat-release | sed -r 's/.* ([0-9]+)\..*/\1/'`
if [ $tempVersion -ne 7 ];then
    logError "$0 仅支持CentOS 7.x"
fi

# 判断agent路径是否正确
mcPath=$PWD
if [ $mcPath != "/root/MagiCude" ]; then
    logErrorNotExit "$0 执行路径非/root/MagiCude"
    logError "请确认已在root目录下新建MagiCude目录"
fi

# 判断agent所需文件是否存在
existFlag=0
dependArrays=("agent.yml" "agentapp.jar" "runAgent.sh" "stopAgent.sh" "util.sh")
for dependName in ${dependArrays[@]} ; do
    if [ ! -f $dependName ] ;then
        logErrorNotExit "MagiCude 目录下未检测到 $dependName"
        logErrorNotExit "请确认已复制 $dependName 到MagiCude目录下"
        existFlag=1
    fi
done
if [ $existFlag -eq 1 ] ;then
    exit 1
fi

# 如果$openjdkDirName已解压，证明已经运行过部署脚本
if [ -d $openjdkDirName ]; then
    logWarn "检测到已运行过部署脚本"
    echo -n "是否继续(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes != "y" ]] ; then
        echo
        logInfo "退出部署"
        exit 1
    fi
fi
logInfo "开始部署"
# kill agentapp
tempPid=`ps -ef|grep agentapp|grep -v grep|cut -c 9-15`
if [ $tempPid ] ;then
    kill -9 $tempPid
fi
# kill nmap masscan
existFlag=`ps -ef|grep nmap|grep -v grep| wc -L`
if [ $existFlag -ne 0 ] ;then
    kill -9 $(pidof nmap)
fi
existFlag=` ps -ef|grep masscan|grep -v grep| wc -L`
if [ $existFlag -ne 0 ] ;then
    kill -9 $(pidof masscan)
fi

function check_ip() {
     local agentRealIp=$1
     VALID_CHECK=$( echo $agentRealIp| awk  -F.  '$1<=255&&$2<=255&&$3<=255&&$4<=255{print "yes"}' )
     if echo $agentRealIp| grep  -E  "^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$"  > /dev/null ;  then
        if [[ $VALID_CHECK =  "yes" ]];  then
            logInfo "正在ping $agentRealIp"
            check_ret=`ping ${agentRealIp} -c 2 | grep -q  'ttl='  && echo "yes" || echo "no"`
            if [ $check_ret = "no" ];then
                logErrorNotExit "$agentRealIp 无法ping通"
                return 1
            else
                logInfo "替换agent.yml中的127.0.0.1"
                sed -i "s/127.0.0.1/$agentRealIp/g" agent.yml
                return 0
            fi
        else
            logErrorNotExit "$agentRealIp 无效的IP"
            return  1
        fi
    else
        logErrorNotExit "$agentRealIp 格式错误"
        return  1
    fi
}

# 判断agent.yml内容是否已经修改
# agent分布式部署
if [ ! -f initCenterEnvironmentAndStart.sh ]; then
    # 修改agent.yml
    logInfo "检测到agent分布式部署"
    # 需要手动输入服务器的ip地址
    existFlag=`cat agent.yml | grep "name: agent1" |wc -L`
    if [ $existFlag -ne 0 ] ;then
        logInfo "修改agent.yml中的name: agent1"
        logWarn "要求：不能是agent1, 需唯一且只能英文且不能包含空格"
        echo -n "请输入agent的名字: "
        read newName
        logInfo "替换agent.yml中的name: agent1"
        sed -i "s/name: agent1/name: $newName/g" agent.yml
    fi

    logInfo "修改agent.yml中center相关的ip"
    existFlag=`cat agent.yml | grep "127.0.0.1" |wc -L`
    if [ $existFlag -ne 0 ] ;then
        while  true ;  do
        echo -n "请输入部署center的服务IP地址: "
        read agentRealIp
        check_ip "${agentRealIp}"
        if [ $? -eq  0 ]; then
           break
        fi
        done
    fi
    
    existFlag=`cat agent.yml | grep "name: agent1" |wc -L`
    if [ $existFlag -ne 0 ] ;then
        logError "agent.yml文件name节点 agent1 未修改，请修改成非 agent1 后重新运行 $0"
    fi
    existFlag=`cat agent.yml | grep "host: 127.0.0.1" |wc -L`
    if [ $existFlag -ne 0 ] ;then
        logErrorNotExit "agent.yml文件rabbitmq或redis的host节点 127.0.0.1 未修改"
        logError "请将 127.0.0.1 修改成 部署centerapp.jar服务器的ip 后重新运行 $0"
    fi
    existFlag=`cat agent.yml | grep "127.0.0.1:8686/eureka" |wc -L`
    if [ $existFlag -ne 0 ] ;then
        logErrorNotExit "agent.yml文件defaultZone节点 127.0.0.1 未修改"
        logError "请将 127.0.0.1 修改成 部署eurekaapp.jar服务器的ip 后重新运行 $0"
    fi
fi

infoMessage=()
if [ -f agent.yml ];then
    existFlag=`cat agent.yml | grep "httpValidateApi: http://127.0.0.1:9001/center/pluginchecker" |wc -L`
    if [ $existFlag -ne 0 ] ;then
        infoMessage=("agent.yml文件center节点httpValidateApi未修改：请将httpValidateApi修改成部署centerapp.jar服务器的ip")
    fi
    existFlag=`cat agent.yml | grep "dnsValidateIp: 127.0.0.1" |wc -L`
    if [ $existFlag -ne 0 ] ;then
        infoMessage+=("agent.yml文件center节点dnsValidateIp未修改：请将dnsValidateIp修改成部署centerapp.jar服务器的ip")
    fi
fi

logInfo "判断是否存在java 环境"
type java >/dev/null 2>&1
if [ $? -eq 0 ];then
    # 存在java环境
    java_version=`java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}'`
    if [ $java_version != $openjdkCheckVersion ];then
        logError "当前已存在java $java_version 环境，请将其卸载后，重新执行 $0"
    fi
fi

# 更换源
existFlag=`cat /etc/yum.repos.d/CentOS-Base.repo | grep ustc |wc -L`
if [ $existFlag -eq 0 ] ;then
    echo -n "系统非中科大源，是否修改成中科大源(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes = "y" ]] ; then
        logInfo "备份源并更换成中科大源"
        sed -e 's|^mirrorlist=|#mirrorlist=|g' -e 's|^#baseurl=http://mirror.centos.org/centos|baseurl=https://mirrors.ustc.edu.cn/centos|g' -i.backup /etc/yum.repos.d/CentOS-Base.repo >/dev/null 2>&1
        yum clean all >/dev/null 2>&1
        yum makecache >/dev/null 2>&1
    else echo
    fi
fi

logInfo "更新系统，时间可能较久"
yum -y update >/dev/null 2>&1
logInfo "安装依赖，时间可能较久"
yum -y install gcc make libpcap libpcap-dev clang git wget >/dev/null 2>&1
logInfo "验证依赖是否成功安装"
dependArrays=("wget" "make" "gcc" "clang" "git")
for dependName in ${dependArrays[@]} ; do
    type $dependName >/dev/null 2>&1
    if [ $? -ne 0 ];then
        logError "$dependName未成功安装，请重新执行 $0"
    fi
done

# 选择Python版本
pythonVerion=${pythonVersionArrays[0]}
# echo "请输入数字选择编译安装的Python版本"
# i=1
# while ( [ $i -le ${#pythonVersionArrays[*]} ] )
# do
#     echo "$i Python ${pythonVersionArrays[i-1]}"
#     let "i++"
# done
# echo -n "10秒后默认选第 1 项 : "
# read -t 10 choice
# if [ ! $choice ]; then
#     choice=1
# fi
# if [[ $choice -gt ${#pythonVersionArrays[*]} ]] || [[ $choice -lt 1 ]]; then
#     choice=1
# fi
# let "choice--"
# pythonVerion="${pythonVersionArrays[choice]}"
# echo
pythonName="Python"
pythonNameVerion=$pythonName-$pythonVerion
pythonTarName=".tar.xz"
logInfo "编译安装$pythonNameVerion"
yum -y install zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gdbm-devel db4-devel libpcap-devel xz-devel libffi-devel zlib1g-dev zlib* >/dev/null 2>&1
if [ ! -f $pythonNameVerion$pythonTarName ]; then
    i=0
    while ( [ $i -lt 5 ] )
    do
        # wget https://www.python.org/ftp/python/$pythonVerion/$pythonNameVerion$pythonTarName
        wget https://mirrors.huaweicloud.com/python/$pythonVerion/$pythonNameVerion$pythonTarName >/dev/null 2>&1
        let "i++"
        if [ -f $pythonNameVerion$pythonTarName ]; then
            break
        fi 
    done
    if [ $i -eq 5 ] ;then
        logError "$pythonNameVerion下载失败，重试第$i次失败，请重新执行 $0"
    fi
fi
type python3 >/dev/null 2>&1
if [ $? -ne 0 ];then
    tar -xJf $pythonNameVerion$pythonTarName
    mkdir /usr/local/python3  >/dev/null 2>&1
    cd $pythonNameVerion
    ./configure --prefix=/usr/local/python3 --enable-shared >/dev/null 2>&1
    make >/dev/null 2>&1
    make install >/dev/null 2>&1
    ln -s /usr/local/python3/bin/python3 /usr/local/bin/python3 >/dev/null 2>&1
    ln -s /usr/local/python3/bin/pip3 /usr/local/bin/pip3 >/dev/null 2>&1
    cd ..
    rm -rf $pythonNameVerion >/dev/null 2>&1
fi
logInfo "判断$pythonNameVerion是否成功安装"
type python3 >/dev/null 2>&1
if [ $? -ne 0 ];then
    logError "$pythonNameVerion 未成功安装，请重新执行 $0"
fi
logInfo "替换agent.yml中的jep.absolutePath的python路径"
sed -i "s/python3.9/python${pythonVerion:0:3}/g" agent.yml
# logInfo "升级pip到最新"
# python3 -m pip install --upgrade pip >/dev/null 2>&1
logInfo "关闭防火墙"
systemctl stop firewalld.service >/dev/null 2>&1 # firewalld
systemctl disable firewalld.service >/dev/null 2>&1 # firewalld
systemctl stop iptables.service >/dev/null 2>&1 # iptables
systemctl disable iptables.service >/dev/null 2>&1 # iptables

# 判断是否存在$openjdk，不存在则下载
if [ ! -f $openjdk ]; then
    logInfo "未检测到$openjdk"
    logInfo "下载$openjdk"
    yum -y install wget >/dev/null 2>&1
    i=0
    while ( [ $i -lt 5 ] )
    do
        # http://jdk.java.net/
        wget https://mirrors.huaweicloud.com/openjdk/$openjdkDownloadVersion/$openjdk >/dev/null 2>&1
        let "i++"
        if [ -f $openjdk ]; then
            break
        fi
    done
    if [ $i -eq 5 ] ;then
        logError "$openjdk下载失败，重试第$i次失败，请重新执行 $0"
    fi
fi
if [ ! -d $openjdkDirName ]; then
    logInfo "解压$openjdk"
    tar -xf $openjdk
fi
# 判断是否已设置环境变量
existFlag=`cat /root/.bash_profile | grep "/root/MagiCude/$openjdkDirName" |wc -L`
if [ $existFlag -eq 0 ] ;then
    logInfo "设置环境变量"
cat <<EOF >> /root/.bash_profile
export JAVA_HOME=/root/MagiCude/$openjdkDirName
export JRE_HOME=\$JAVA_HOME/jre
export CLASSPATH=\$JAVA_HOME/lib:\$JRE_HOME/lib:\$CLASSPATH
export PATH=\$JAVA_HOME/bin:\$JRE_HOME/bin:\$PATH
export LD_LIBRARY_PATH=/usr/local/python3/lib
EOF
fi
# 下次登录才生效
source /root/.bash_profile
logInfo "安装JEP"
pip3 install wheel -i https://pypi.douban.com/simple/ >/dev/null 2>&1
pip3 install jep -i https://pypi.douban.com/simple/ >/dev/null 2>&1

logInfo "安装nmap和masscan"
type nmap >/dev/null 2>&1
if [ $? -ne 0 ];then
    rpm -U $nmapUrl >/dev/null 2>&1
fi
type masscan >/dev/null 2>&1
if [ $? -ne 0 ];then
    if [ ! -d masscan ]; then
        i=0
        while ( [ $i -lt 5 ] )
        do
            git clone https://github.com/robertdavidgraham/masscan.git >/dev/null 2>&1
            let "i++"
            if [ -d masscan ]; then
                break
            fi
        done
        if [ $i -eq 5 ] ;then
            logError "masscan下载失败，重试第$i次失败，请重新执行 $0"
        fi
    fi
    cd masscan
    make >/dev/null 2>&1
    make install >/dev/null 2>&1
    cd ..
    rm -rf masscan >/dev/null 2>&1
fi

logInfo "验证nmap masscan是否成功安装"
dependArrays=("masscan" "nmap")
for dependName in ${dependArrays[@]} ; do
    type $dependName >/dev/null 2>&1
    if [ $? -ne 0 ];then
        logError "$dependName未成功安装，请重新执行 $0"
    fi
done

logInfo "初始化环境结束"
logInfo "执行sh runAgent.sh"
sh runAgent.sh

if [ ${#infoMessage[*]} -ne 0 ];then 
    echo
    logWarn "以下信息不会影响魔方正常运行，但可能会导致agent部分功能不可用，请根据提示进行修改，并重启agent"
    logWarn "info start"
    for info in ${infoMessage[@]} ; do
        echo -e "$info"
    done
    logWarn "info end"
fi