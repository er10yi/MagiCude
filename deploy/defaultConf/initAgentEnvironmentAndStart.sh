#!/bin/bash
# 初始化环境
# @author 贰拾壹
# https://github.com/er10yi

echo
echo "魔方-MagiCude agent部署脚本 V2.4"
echo "@author 贰拾壹"
echo "https://github.com/er10yi"
echo

# 如果jdk-15已解压，证明已经运行过部署脚本
if [ -d "jdk-15" ]; then
    echo -e "检测到已运行过部署脚本"
    echo -n "是否继续(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes != "y" ]] ; then
        echo
        echo "**********退出部署**********"
        exit 1
    fi
fi
echo "**********开始部署**********"
# kill agentapp
tempPid=`ps -ef|grep agentapp|grep -v grep|cut -c 9-15`
if [ $tempPid ] ;then
    kill -9 $tempPid
fi
# kill nmap masscan
existFlag=`ps -ef|grep nmap|grep -v grep|cut -c 9-15`
if [ $existFlag ] ;then
    kill -9 $(pidof nmap)
fi
existFlag=`ps -ef|grep masscan|grep -v grep|cut -c 9-15`
if [ $existFlag ] ;then
    kill -9 $(pidof masscan)
fi

echo "**********判断是否存在java 环境**********"
java -version
if [ $? -eq 0 ];then
    # 存在java环境
    java_version=`java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}'`
    if [ $java_version != "15" ];then
        echo "!!!!!!!!!! 当前已存在java $java_version 环境 !!!!!!!!!!"
        echo "请将其卸载后，重新执行 $0 "
        exit 1
    fi
fi

# 更换源
existFlag=`cat /etc/yum.repos.d/CentOS-Base.repo | grep ustc |wc -L`
if [ $existFlag -eq 0 ] ;then
    echo -n "系统非中科大源，是否修改成中科大源(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes = "y" ]] ; then
        echo "**********备份源并更换成中科大源**********"
        temp=`sed -e 's|^mirrorlist=|#mirrorlist=|g' -e 's|^#baseurl=http://mirror.centos.org/centos|baseurl=https://mirrors.ustc.edu.cn/centos|g' -i.backup /etc/yum.repos.d/CentOS-Base.repo`
        temp=`yum clean all && yum makecache`
    else echo
    fi
fi

echo "**********更新系统，时间可能较久**********"
echo "**********更新或安装依赖出现超时请忽略**********"
temp=`yum -y update`
echo "**********安装依赖**********"
temp=`yum -y install gcc make libpcap libpcap-dev clang git wget`
echo "**********验证依赖是否成功安装**********"
dependArrays=("wget" "make" "gcc" "clang" "git")
for dependName in ${dependArrays[@]} ; do
    existFlag=`ls /usr/bin/ | grep $dependName |wc -L`
    if [ $existFlag -eq 0 ] ;then
        echo "!!!!!!!!!! $dependName未成功安装，请重新执行 $0 !!!!!!!!!!"
        exit 1
    fi
done

pythonVerion="Python-3.8.5"
pythonTarName=".tar.xz"
echo "**********编译安装$pythonVerion**********"
temp=`yum -y install zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gdbm-devel db4-devel libpcap-devel xz-devel libffi-devel zlib1g-dev zlib*`
if [ ! -f $pythonVerion$pythonTarName ]; then
    echo "下载$pythonVerion"
    i=0
    while ( [ $i -lt 5 ] )
    do
        # wget https://www.python.org/ftp/python/3.8.5/Python-3.8.5.tar.xz
        wget https://mirrors.huaweicloud.com/python/3.8.5/$pythonVerion$pythonTarName
        let "i++"
        if [ -f $pythonVerion$pythonTarName ]; then
            break
        fi
    done
    if [ $i -eq 5 ] ;then
        echo "!!!!!!!!!! $pythonVerion下载失败，重试第$i次失败，请重新执行 $0 !!!!!!!!!!"
        exit 1
    fi
fi
echo "**********判断$pythonVerion是否已安装**********"
python3 --version
if [ $? -ne 0 ];then
    echo "$pythonVerion未安装"
    tar -xJf $pythonVerion$pythonTarName
    mkdir /usr/local/python3 
    cd $pythonVerion
    ./configure --prefix=/usr/local/python3 --enable-shared 
    make && make install
    temp=`ln -s /usr/local/python3/bin/python3 /usr/local/bin/python3`
    temp=`ln -s /usr/local/python3/bin/pip3 /usr/local/bin/pip3`
    cd ..
    temp=`rm -rf $pythonVerion`
fi
export LD_LIBRARY_PATH=/usr/local/python3/lib
echo "**********判断$pythonVerion是否成功安装**********"
python3 --version
if [ $? != 0 ];then
    echo "!!!!!!!!!! $pythonVerion 未成功安装，请重新执行 $0 !!!!!!!!!!"
    exit 1
fi
echo "**********升级pip到最新**********"
temp=`python3 -m pip install --upgrade pip`
echo "**********关闭防火墙**********"
systemctl stop firewalld.service # firewalld
systemctl disable firewalld.service # firewalld
systemctl stop iptables.service # iptables
systemctl disable iptables.service # iptables

# 判断是否存在openjdk-15_linux-x64_bin.tar.gz，不存在则下载
openjdk="openjdk-15_linux-x64_bin.tar.gz"
if [ ! -f $openjdk ]; then
    echo "**********未检测到$openjdk**********"
    echo "下载$openjdk"
    temp=`yum -y install wget`
    i=0
    while ( [ $i -lt 5 ] )
    do
        # http://jdk.java.net/
        wget https://mirrors.huaweicloud.com/openjdk/15/$openjdk
        let "i++"
        if [ -f $openjdk ]; then
            break
        fi
    done
    if [ $i -eq 5 ] ;then
        echo "!!!!!!!!!! $openjdk下载失败，重试第$i次失败，请重新执行 $0 !!!!!!!!!!"
        exit 1
    fi
fi
if [ ! -d "jdk-15" ]; then
    echo "**********解压openjdk-15**********"
    tar -xf $openjdk
fi
echo "**********设置环境变量**********"
cat <<EOF >> /root/.bash_profile
export JAVA_HOME=/root/MagiCude/jdk-15
export JRE_HOME=\$JAVA_HOME/jre
export CLASSPATH=\$JAVA_HOME/lib:\$JRE_HOME/lib:\$CLASSPATH
export PATH=\$JAVA_HOME/bin:\$JRE_HOME/bin:\$PATH
export LD_LIBRARY_PATH=/usr/local/python3/lib
EOF
# 下次登录才生效
source /root/.bash_profile
# 需要再次执行，本次编译用到
export JAVA_HOME=/root/MagiCude/jdk-15
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib:$CLASSPATH
export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH
export LD_LIBRARY_PATH=/usr/local/python3/lib
echo "**********安装JEP**********"
pip3 install wheel -i https://pypi.douban.com/simple/
pip3 install jep -i https://pypi.douban.com/simple/

echo "**********安装nmap和masscan**********"
cd agentDependency
existFlag=`ls /usr/bin/ | grep nmap |wc -L`
if [ $existFlag -eq 0 ] ;then
    temp=`rpm -ivh nmap-7.80-1.x86_64.rpm`
fi
existFlag=`ls /usr/bin/ | grep masscan |wc -L`
if [ $existFlag -eq 0 ] ;then
    rm -rf masscan
    temp=`rpm -ivh libpcap-devel-1.5.3-12.el7.x86_64.rpm`
    #tar -xf masscan.tar.gz
    if [ ! -d masscan ]; then
        i=0
        while ( [ $i -lt 5 ] )
        do
            git clone https://github.com/robertdavidgraham/masscan.git
            let "i++"
            if [ -d masscan ]; then
                break
            fi
        done
        if [ $i -eq 5 ] ;then
            echo "!!!!!!!!!! masscan下载失败，重试第$i次失败，请重新执行 $0 !!!!!!!!!!"
            exit 1
        fi
    fi
    cd masscan
    temp=`make & make install`
    cd ..
    temp=`rm -rf masscan`
fi
cd ..

echo "**********验证nmap masscan是否成功安装**********"
dependArrays=("masscan" "nmap")
for dependName in ${dependArrays[@]} ; do
    existFlag=`ls /usr/bin/ | grep $dependName |wc -L`
    if [ $existFlag -eq 0 ] ;then
        echo "!!!!!!!!!! $dependName未成功安装，请重新执行 $0 !!!!!!!!!!"
        exit 1
    fi
done

echo "**********初始化环境结束**********"
echo "**********执行sh runAgent.sh**********"
sh runAgent.sh