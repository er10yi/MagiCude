#!/bin/bash
# 初始化环境
# @author 贰拾壹
# https://github.com/er10yi
source /root/MagiCude/util.sh

echo
echo "魔方-MagiCude 一键部署脚本 V2.6"
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

# 判断center路径是否正确
mcPath=$PWD
if [ $mcPath != "/root/MagiCude" ]; then
    logErrorNotExit "$0 执行路径非/root/MagiCude"
    logError "请确认已将deploy目录下的MagiCude复制root目录下"
fi
# 判断center所需文件是否存在
existFlag=0
if [ ! -f db/magicude.sql ] ;then
    logErrorNotExit "db 目录下未检测到 magicude.sql"
    existFlag=1
fi
dependArrays=("centerapp.jar" "eurekaapp.jar" "center.yml" "eureka.yml")
for dependName in ${dependArrays[@]} ; do
    if [ ! -f $dependName ] ;then
        logErrorNotExit "MagiCude 目录下未检测到 $dependName"
        existFlag=1
    fi
done
if [ $existFlag -eq 1 ] ;then
    exit 1
fi

# 如果$openjdkDirName已解压，证明已经运行过部署脚本
if [ -d $openjdkDirName ]; then
    logWarn "检测到已运行过部署脚本"
    logWarn "继续运行脚本将重置数据库，当前数据会丢失"
    logWarn "如已正常部署，请勿执行"
    echo -n "是否继续(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes != "y" ]] ; then
        echo
        logInfo "退出部署"
        exit 1
    fi
fi
logInfo "开始部署"
# kill all jar
jarNameArrays=("eurekaapp" "centerapp" "agentapp")
for jarName in ${jarNameArrays[@]} ; do
    tempPid=`ps -ef|grep $jarName|grep -v grep|cut -c 9-15`
    if [ $tempPid ] ;then
        kill -9 $tempPid
    fi
done
# kill nmap masscan
existFlag=`ps -ef|grep nmap|grep -v grep| wc -L`
if [ $existFlag -ne 0 ] ;then
    kill -9 $(pidof nmap)
fi
existFlag=` ps -ef|grep masscan|grep -v grep| wc -L`
if [ $existFlag -ne 0 ] ;then
    kill -9 $(pidof masscan)
fi

# stop and remove docker images
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

# 修改前端api地址及agent.yml
errorMessage=()
existFlag=`cat dist/static/js/app.*.js | grep "http://127.0.0.1:9001" |wc -L`
if [ $existFlag -ne 0 ] ;then
    tempIp=`ip a |grep -w inet|awk '{print $2}'|awk -F '/' '{print $1}'`
    ipArray=(${tempIp// / })
    logInfo "服务器所有IP如下: "
    i=1
    while ( [ $i -le ${#ipArray[*]} ] )
    do
        echo "$i ${ipArray[i-1]}"
        let "i++"
    done
    echo -n "请输入数字选择部署center的IP地址: "
    read choice
    if [ ! $choice ]; then
        logErrorNotExit "未选择有效的IP地址"
        logError "请重新执行 $0"
    fi
    if [[ $choice -gt ${#ipArray[*]} ]] || [[ $choice -lt 1 ]]; then
        logErrorNotExit "未选择有效的IP地址"
        logError "请重新执行 $0"
    fi
    let "choice--"
    centerRealIp="${ipArray[choice]}"
    logInfo "centerip写入centerip.bak文件"
    echo $centerRealIp > centerip.bak
    logInfo "替换前端api地址"
    sed -i "s/127.0.0.1/$centerRealIp/g" dist/static/js/app.*.js
    logInfo "替换agent.yml中的地址"
    sed -i "s/httpValidateApi: http:\/\/127.0.0.1/httpValidateApi: http:\/\/$centerRealIp/g" agent.yml
    sed -i "s/dnsValidateIp: 127.0.0.1/dnsValidateIp: $centerRealIp/g" agent.yml
fi

# 判断前端api地址是否修改
errorMessage=()
existFlag=`cat dist/static/js/app.*.js | grep "http://127.0.0.1:9001" |wc -L`
if [ $existFlag -ne 0 ] ;then
    errorMessage+=("检测到前端api地址未修改，如果是远程部署，将无法通过ip地址访问魔方-MagiCude，请修改前端api地址，并重启魔方")
fi
# 判断agent.yml内容是否已经修改
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
else
    logError "agent.yml不存在"
fi

logInfo "判断是否存在java环境"
type java >/dev/null 2>&1
if [ $? -eq 0 ];then
    # 存在java环境
    java_version=`java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}'`
    if [ $java_version != $openjdkVersion ];then
        logErrorNotExit "当前已存在 java $java_version 环境"
        logError "请将其卸载后，重新执行 $0"
    fi
fi

# 更换源
existFlag=`cat /etc/yum.repos.d/CentOS-Base.repo | grep ustc |wc -L`
if [ $existFlag -eq 0 ] ;then
    echo -n "系统非中科大源，是否修改成中科大源(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes = "y" ]] ; then
        logInfo "备份源并更换成中科大源"
        sed -e 's|^mirrorlist=|#mirrorlist=|g' -e 's|^#baseurl=http://mirror.centos.org/centos|baseurl=https://mirrors.ustc.edu.cn/centos|g' -i.backup /etc/yum.repos.d/CentOS-Base.repo
        yum clean all >/dev/null 2>&1
        yum makecache >/dev/null 2>&1
    else echo
    fi
fi

logInfo "更新系统，时间可能较久"
yum -y update >/dev/null 2>&1
logInfo "安装依赖，时间可能较久"
yum -y install wget fontconfig stix-fonts ntpdate docker gcc make libpcap libpcap-dev clang git >/dev/null 2>&1
logInfo "验证依赖是否成功安装"
dependArrays=("wget" "docker" "make" "gcc" "clang" "git")
for dependName in ${dependArrays[@]} ; do
    type $dependName >/dev/null 2>&1
    if [ $? -ne 0 ];then
        logError "$dependName 未成功安装，请重新执行 $0"
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
    rm -rf $pythonNameVerion
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
logInfo "SELinux切换成Permissive"
setenforce 0
logInfo "修改时区并同步时间"
timedatectl set-timezone Asia/Shanghai
ntpdate cn.pool.ntp.org >/dev/null 2>&1

# 判断是否存在$openjdk，不存在则下载
if [ ! -f $openjdk ]; then
    logInfo "未检测到$openjdk"
    logInfo "下载$openjdk"
    i=0
    while ( [ $i -lt 5 ] )
    do
        # http://jdk.java.net/
        wget https://mirrors.huaweicloud.com/openjdk/$openjdkVersion/$openjdk >/dev/null 2>&1
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

logInfo "docker"
# docker pull镜像并验证
existFlag=`cat /etc/docker/daemon.json | grep ustc |wc -L` >/dev/null 2>&1
if [ $existFlag -eq 0 ] ;then
    echo -n "docker非中科大镜像，是否修改成中科大镜像(10秒后默认N)? [y/N]: "
    read -t 10 checkYes
    if [[ $checkYes = "y" ]] ; then
        logInfo "docker更换成中科大镜像"
        echo -e "{\n\"registry-mirrors\": [\"https://docker.mirrors.ustc.edu.cn\"]\n}" > "/etc/docker/daemon.json"
        systemctl daemon-reload  >/dev/null 2>&1
        systemctl restart docker
    else echo
    fi
fi
systemctl start docker
systemctl enable docker >/dev/null 2>&1

# 偷个懒，只判断nginx是否pull
# 如果没有证明是第一次执行
# 则全部都要pull
existFlag=`docker images | grep nginx |wc -L`
if [ $existFlag -eq 0 ] ;then
    logInfo "docker拉取所需镜像，时间可能较久"
    docker pull nginx >/dev/null 2>&1 &
    docker pull mysql >/dev/null 2>&1 &
    docker pull redis >/dev/null 2>&1 &
    docker pull rabbitmq:management >/dev/null 2>&1 &
    wait
fi

# 如果未成功pull，会尝试再pull
logInfo "检查docker本地容器是否已拉取"
for i in `seq 1 5`
do
    dockerNameArrays=("nginx" "mysql" "redis" "rabbitmq")
    for imageName in ${dockerNameArrays[@]} ; do
        existFlag=`docker images | grep $imageName |wc -L`
        if [ $existFlag -eq 0 ] ;then
            if [ $imageName = "rabbitmq" ]; then 
                imageName="rabbitmq:management"
            fi
            logWarn "$imageName 不存在，正在重新pull $imageName"
            docker pull $imageName >/dev/null 2>&1 &
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
                    logErrorNotExit "pull $imageName 失败"
                    logError "请重新执行 $0"
                fi
            fi
            docker ps | grep $imageName
        # else logInfo "$imageName 已存在"
        fi
    done
done

logInfo "docker创建并运行容器"
docker run -di --name magicude_mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=8TAQRc9EOkV607qm mysql >/dev/null 2>&1
docker run -di --name magicude_redis  -p 6379:6379 redis --requirepass snclGVwsAywx1G2R >/dev/null 2>&1
docker run -di --name magicude_rabbitmq -p 5671:5671 -p 5672:5672 -p 4369:4369 -p 15671:15671 -p 15672:15672 -p 25672:25672 rabbitmq:management >/dev/null 2>&1
docker run -di --name nginxApp -p 80:80 -v $PWD/dist/:/usr/share/nginx/dist/ -v $PWD/nginx/default.conf:/etc/nginx/conf.d/default.conf -d nginx >/dev/null 2>&1
sleep 5s
# checkDockerImageStatus.sh判断容器如果未成功启动，会尝试启动一次
sh operation/checkDockerImageStatus.sh
sleep 10s

logInfo "确保容器服务已成功运行"
# mysql
i=1
while ( [ -z "`docker exec -it magicude_mysql /bin/bash -c "mysql -uroot -p8TAQRc9EOkV607qm -e'show databases'" | grep "information_schema"`" ] && [ $i -lt 6 ] )
do
    logWarn "magicude_mysql未启动，第$i次重启"
    docker start magicude_mysql >/dev/null 2>&1
    let "i++"
    sleep 10s
done
if [ $i -eq 6 ] ;then
    logError "docker 启动 magicude_mysql失败，重试第$i次失败，请重新执行 $0"
else
    logInfo "magicude_mysql成功启动"
fi

# 其他
dockerNameArrays=("nginxApp" "magicude_redis" "magicude_rabbitmq")
for imageName in ${dockerNameArrays[@]} ; do
    i=1
    while ( [ -z "`docker exec -it $imageName /bin/bash -c "ls /" | grep "root"`" ] && [ $i -lt 6 ] )
    do
        logWarn "$imageName未启动，第$i次重启"
        docker start $imageName >/dev/null 2>&1
        let "i++"
        sleep 10s
    done
    if [ $i -eq 6 ] ;then
        logError "docker 启动 $imageName失败，重试第$i次失败，请重新执行 $0"
    else
        logInfo "$imageName成功启动"
    fi
done
# 再次确保容器已经启动
sh operation/checkDockerImageStatus.sh
sleep 10s

logInfo "magicude 增加执行权限"
chmod +x magicude
logInfo "初始化环境结束"
logInfo "初始化数据并启动魔方"
sh initDataAndStart.sh

if [ ${#infoMessage[*]} -ne 0 ];then
    echo
    logWarn "以下信息不会影响魔方正常运行，但可能会导致部分功能不可用，请根据提示进行修改，并重启魔方"
    logWarn "info start"
    for info in ${infoMessage[@]} ; do
        echo -e "$info"
    done
    logWarn "info end"
fi
if [ ${#errorMessage[*]} -ne 0 ];then
    echo
    logErrorNotExit "以下错误会影响魔方正常运行，导致魔方不可用，请根据提示进行修改，并重启魔方"
    logErrorNotExit "error start"
    for error in ${errorMessage[@]} ; do
        echo -e "$error"
    done
    logErrorNotExit "error end"
fi
