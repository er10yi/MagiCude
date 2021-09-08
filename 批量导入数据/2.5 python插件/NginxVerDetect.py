# NginxVerDetect||低危||3000|||nginx|out|Nginx版本低于最新版/已不受支持|
# -*- coding:utf-8 -*-

def check(ip, domain, port, args, timeout, payload_map):
    nginx_mainline_version = payload_map.get('Mainline version')[0]
    nginx_stable_version = payload_map.get('Stable version')[0]
    nginx_raw_version = payload_map.get('rawVersion')[0]
    split_version = nginx_raw_version.split(' ')
    if len(split_version) == 2:
        real_version = nginx_raw_version.split(' ')[1]
        if real_version is not None:
            real_cp_mainline = compare_version(
                nginx_mainline_version, real_version)
            real_cp_stable = compare_version(
                nginx_stable_version, real_version)
            if real_cp_mainline == 0 or real_cp_stable == 0:
                return nginx_raw_version + ' is up to date'
            else:
                # 小于主线版本
                if real_cp_mainline == 1:
                    return nginx_raw_version + ' is out of date. ' + 'Mainline version: ' + nginx_mainline_version
                # 小于稳定版
                if real_cp_stable == 1:
                    return nginx_raw_version + ' is out of date. ' + 'Stable version: ' + nginx_stable_version


# 0相等，1左边大，-1右边大
# version1----第一个要比较的版本字符串
# version2----第二个要比较的版本字符串
# split_flag----版本分隔符，默认为"."，可自定义
# 接受的版本字符形式----空/x/x.y/x.y./x.y.z；两个参数可为前边列出的形式的任一种
# https://blog.csdn.net/wys5wys/article/details/90271155
def compare_version(version1=None, version2=None, split_flag="."):
    # 如果存在有为空的情况则进入
    if (version1 is None) or (version1 == "") or (version2 is None) or (version2 == ""):
        # version1为空且version2不为空，则返回version2大
        if ((version1 is None) or (version1 == "")) and (version2 is not None) and (version2 != ""):
            return -1
        # version2为空且version1不为空，则返回version1大
        if ((version2 is None) or (version2 == "")) and (version1 is not None) and (version1 != ""):
            return 1

    # 如果版本字符串相等，那么直接返回相等，这句会且只会在第一次比较时才可能进入
    # version1和version2都为空时也会进入这里
    if version1 == version2:
        return 0

    # 对版本字符串从左向右查找"."，第一个"."之前的字符串即为此次要比较的版本
    # 如1.3.5中的1
    try:
        current_section_version1 = version1[:version1.index(split_flag)]
    except:
        current_section_version1 = version1
    try:
        current_section_version2 = version2[:version2.index(split_flag)]
    except:
        current_section_version2 = version2
    # 对本次要比较的版本字符转成整型进行比较
    if int(current_section_version1) > int(current_section_version2):
        return 1
    elif int(current_section_version1) < int(current_section_version2):
        return -1

    # 如果本次传来版本字符串中已没有版本号分隔符，那说明本次比较的版本号已是最后一位版本号，下次比较值赋空
    # 如本次传来的是5，那下次要比较的只能赋空
    try:
        other_section_version1 = version1[version1.index(split_flag) + 1:]
    except:
        other_section_version1 = ""
    try:
        other_section_version2 = version2[version2.index(split_flag) + 1:]
    except:
        other_section_version2 = ""

    # 递归调用比较
    return compare_version(other_section_version1, other_section_version2)
