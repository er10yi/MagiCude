#!/bin/bash
# 初始化环境
# @author 贰拾壹
# https://github.com/er10yi

echo
echo "魔方-MagiCude 一键部署脚本 V2.4"
echo "@author 贰拾壹"
echo "https://github.com/er10yi"
echo

# 如果jdk-15已解压，证明已经运行过部署脚本
if [ -d "jdk-15" ]; then
    echo -e "检测到已运行过部署脚本\n继续运行脚本将重置数据库，当前数据会丢失\n如已正常部署，请勿执行"
    echo -n "是否继续(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes != "y" ]] ; then
        echo
        echo "**********退出部署**********"
        exit 1
    fi
fi
echo "**********开始部署**********"
# kill all jar
jarNameArrays=("eurekaapp" "centerapp" "agentapp")
for jarName in ${jarNameArrays[@]} ; do
    tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
    if [ $tempPid ] ;then
        kill -9 $tempPid
    fi
done
# kill nmap masscan
existFlag=`ps -ef|grep nmap|grep -v grep|cut -c 9-15`
if [ $existFlag ] ;then
    kill -9 $(pidof nmap)
fi
existFlag=`ps -ef|grep masscan|grep -v grep|cut -c 9-15`
if [ $existFlag ] ;then
    kill -9 $(pidof masscan)
fi
# stop and remove docker images
existFlag=`ls /usr/bin/ | grep docker |wc -L`
if [ $existFlag -ne 0 ] ;then
    dockerNameArrays=("nginxApp" "magicude_mysql" "magicude_redis" "magicude_rabbitmq")
    temp=`systemctl restart docker`
    for imageName in ${dockerNameArrays[@]} ; do
        existFlag=`docker ps -a | grep $imageName |wc -L`
        if [ $existFlag -ne 0 ] ;then
            temp=`docker stop $imageName`
            temp=`docker rm $imageName`
        fi
    done
    temp=`systemctl stop docker`
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
temp=`yum -y install wget fontconfig stix-fonts ntpdate docker gcc make libpcap libpcap-dev clang git`
echo "**********验证依赖是否成功安装**********"
dependArrays=("wget" "docker" "make" "gcc" "clang" "git")
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
if [ $? -ne 0 ];then
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
echo "**********SELinux切换成Permissive**********"
setenforce 0
echo "**********修改时区并同步时间**********"
timedatectl set-timezone Asia/Shanghai
temp=`ntpdate cn.pool.ntp.org`

# 判断是否存在openjdk-15_linux-x64_bin.tar.gz，不存在则下载
openjdk="openjdk-15_linux-x64_bin.tar.gz"
if [ ! -f $openjdk ]; then
    echo "**********未检测到$openjdk**********"
    echo "下载$openjdk"
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
    echo "**********解压$openjdk**********"
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

echo "**********验证docker nmap masscan是否成功安装**********"
dependArrays=("docker" "masscan" "nmap")
for dependName in ${dependArrays[@]} ; do
    existFlag=`ls /usr/bin/ | grep $dependName |wc -L`
    if [ $existFlag -eq 0 ] ;then
        echo "!!!!!!!!!! $dependName未成功安装，请重新执行 $0 !!!!!!!!!!"
        exit 1
    fi
done

echo "**********docker**********"
# docker pull镜像并验证
existFlag=`cat /etc/docker/daemon.json | grep ustc |wc -L`
if [ $existFlag -eq 0 ] ;then
    echo -n "docker非中科大镜像，是否修改成中科大镜像(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes = "y" ]] ; then
        echo "**********docker更换成中科大镜像**********"
        echo -e "{\n\"registry-mirrors\": [\"https://docker.mirrors.ustc.edu.cn\"]\n}" > "/etc/docker/daemon.json"
        temp=`systemctl daemon-reload`
        temp=`systemctl restart docker`
    else echo
    fi
fi
temp=`systemctl start docker`
temp=`systemctl enable docker`

# 偷个懒，只判断nginx是否pull
# 如果没有证明是第一次执行
# 则全部都要pull
existFlag=`docker images | grep nginx |wc -L`
if [ $existFlag -eq 0 ] ;then
    echo "**********docker拉取所需镜像，时间可能较久**********"
    docker pull nginx &
    docker pull mysql &
    docker pull redis &
    docker pull rabbitmq:management &
    wait
fi

echo "**********验证docker镜像是否已拉取**********"
# 如果未成功pull，会尝试再pull
echo "check docker local repository"
for i in `seq 1 5`
do
    dockerNameArrays=("nginx" "mysql" "redis" "rabbitmq")
    for imageName in ${dockerNameArrays[@]} ; do
        existFlag=`docker images | grep $imageName |wc -L`
        if [ $existFlag -eq 0 ] ;then
            if [ $imageName = "rabbitmq" ]; then 
                imageName="rabbitmq:management"
            fi
            echo "docker $imageName not existed, pulling latest $imageName now"
            docker pull $imageName &
            wait
            if [ $imageName = "rabbitmq" ]; then 
                imageName="rabbitmq"
            fi
            existFlag=`docker images | grep $imageName |wc -L`
            if [ $existFlag -eq 0 ] ;then
                if [ $imageName = "rabbitmq" ]; then 
                    imageName="rabbitmq:management"
                fi
                if [ $i -eq 6 ];then
                    echo "!!!!!!!!!! docker pull $imageName failed. !!!!!!!!!!"
                    echo "!!!!!!!!!! 请重新执行 $0 !!!!!!!!!!"
                    exit 1
                fi
            fi
            docker ps | grep $imageName
        else echo "docker $imageName already existed."
        fi
    done
done

echo "**********docker创建并运行容器**********"
temp=`docker run -di --name magicude_mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=8TAQRc9EOkV607qm mysql`
temp=`docker run -di --name magicude_redis  -p 6379:6379 redis --requirepass snclGVwsAywx1G2R`
temp=`docker run -di --name magicude_rabbitmq -p 5671:5671 -p 5672:5672 -p 4369:4369 -p 15671:15671 -p 15672:15672 -p 25672:25672 rabbitmq:management`
temp=`docker run -di --name nginxApp -p 80:80 -v $PWD/dist/:/usr/share/nginx/dist/ -v $PWD/nginx/default.conf:/etc/nginx/conf.d/default.conf -d nginx`
sleep 5s
# checkDockerImageStatus.sh判断容器如果未成功启动，会尝试启动一次
sh operation/checkDockerImageStatus.sh
sleep 10s

echo "**********确保容器服务已成功运行**********"
# mysql
i=1
while ( [ -z "`docker exec -it magicude_mysql /bin/bash -c "mysql -uroot -p8TAQRc9EOkV607qm -e'show databases'" | grep "information_schema"`" ] && [ $i -lt 6 ] )
do
    echo "magicude_mysql未启动，第$i次重启"
    temp=`docker start magicude_mysql`
    let "i++"
    sleep 10s
done
if [ $i -eq 6 ] ;then
    echo "!!!!!!!!!!docker start magicude_mysql失败，重试第$i次失败，请重新执行 $0 !!!!!!!!!!"
    exit 1
else
    echo "magicude_mysql成功启动"
fi

# 其他
dockerNameArrays=("nginxApp" "magicude_redis" "magicude_rabbitmq")
for imageName in ${dockerNameArrays[@]} ; do
    i=1
    while ( [ -z "`docker exec -it $imageName /bin/bash -c "ls /" | grep "root"`" ] && [ $i -lt 6 ] )
    do
        echo "$imageName未启动，第$i次重启"
        temp=`docker start $imageName`
        let "i++"
        sleep 10s
    done
    if [ $i -eq 6 ] ;then
        echo "!!!!!!!!!!docker start $imageName失败，重试第$i次失败，请重新执行 $0 !!!!!!!!!!"
        exit 1
    else
        echo "$imageName成功启动"
    fi
done
# 再次确保容器已经启动
sh operation/checkDockerImageStatus.sh
sleep 10s

echo "**********magicude 增加执行权限**********"
chmod +x magicude
echo "**********初始化环境结束**********"
echo "**********初始化数据并启动系统**********"
sh initDataAndStart.sh