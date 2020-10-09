#!/bin/bash
# 初始化数据库
# @author 贰拾壹
# https://github.com/er10yi

echo  "*****info*****   docker MySQL导入数据"
mysql -uroot -p8TAQRc9EOkV607qm -e"CREATE DATABASE magicude DEFAULT CHARACTER SET utf8" >/dev/null 2>&1
mysql -uroot -p8TAQRc9EOkV607qm -Dmagicude -e"SOURCE /usr/local/magicude.sql" >/dev/null 2>&1