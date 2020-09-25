#!/bin/bash
# 初始化数据库
# @author 贰拾壹
# https://github.com/er10yi

echo "**********docker MySQL导入数据**********"
temp=`mysql -uroot -p8TAQRc9EOkV607qm -e"CREATE DATABASE magicude DEFAULT CHARACTER SET utf8"`
temp=`mysql -uroot -p8TAQRc9EOkV607qm -Dmagicude -e"SOURCE /usr/local/magicude.sql"`