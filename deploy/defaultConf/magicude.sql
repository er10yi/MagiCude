SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_agent
-- ----------------------------
DROP TABLE IF EXISTS `tb_agent`;
CREATE TABLE `tb_agent`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'agent编号',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'agent名称',
  `nmappath` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'nmap路径',
  `masspath` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'mass路径',
  `ipaddress` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `online` tinyint(1) NULL DEFAULT NULL COMMENT '在线',
  `timeouts` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '超时次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'agent表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_agent
-- ----------------------------

-- ----------------------------
-- Table structure for tb_appsystem
-- ----------------------------
DROP TABLE IF EXISTS `tb_appsystem`;
CREATE TABLE `tb_appsystem`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `accessurl` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问地址',
  `level` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统等级',
  `developdesc` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '研发说明',
  `deploydesc` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部署说明',
  `whitelist` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '白名单访问列表',
  `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tabbitmap` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签bitmap',
  `managerid` bigint NULL DEFAULT NULL COMMENT '管理员编号',
  `devmanagerid` bigint NULL DEFAULT NULL COMMENT '开发负责人编号',
  `opermanagerid` bigint NULL DEFAULT NULL COMMENT '运维负责人编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用系统表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_appsystem
-- ----------------------------

-- ----------------------------
-- Table structure for tb_assetip
-- ----------------------------
DROP TABLE IF EXISTS `tb_assetip`;
CREATE TABLE `tb_assetip`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产ip编号',
  `projectinfoid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目信息编号',
  `ipaddressv4` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ipv4地址',
  `ipaddressv6` varchar(39) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ipv6地址',
  `checkwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '安全检测白名单',
  `assetnotifywhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '资产提醒白名单',
  `activetime` datetime(0) NULL DEFAULT NULL COMMENT 'ip发现时间',
  `passivetime` datetime(0) NULL DEFAULT NULL COMMENT 'ip下线时间',
  `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tabbitmap` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签bitmap',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产ip，用于记录资产ip信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_assetip
-- ----------------------------

-- ----------------------------
-- Table structure for tb_assetip_appsys_hostdomain
-- ----------------------------
DROP TABLE IF EXISTS `tb_assetip_appsys_hostdomain`;
CREATE TABLE `tb_assetip_appsys_hostdomain`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `assetipid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产ip编号',
  `assetportid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产端口编号',
  `appsysid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用系统编号',
  `hostdomainid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主机域名编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产ip域名应用中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_assetip_appsys_hostdomain
-- ----------------------------

-- ----------------------------
-- Table structure for tb_assetport
-- ----------------------------
DROP TABLE IF EXISTS `tb_assetport`;
CREATE TABLE `tb_assetport`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '端口编号',
  `assetipid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产ip编号',
  `port` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '端口',
  `protocol` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口协议',
  `state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口开放状态',
  `service` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口服务',
  `version` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务版本',
  `checkwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '安全检测白名单',
  `assetnotifywhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '提醒白名单',
  `uptime` datetime(0) NULL DEFAULT NULL COMMENT '端口发现时间',
  `downtime` datetime(0) NULL DEFAULT NULL COMMENT '端口关闭时间',
  `changedtime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `tabbitmap` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签bitmap',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产端口，用于记录端口信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_assetport
-- ----------------------------

-- ----------------------------
-- Table structure for tb_categorycomstru
-- ----------------------------
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
DROP TABLE IF EXISTS `tb_categorycomstru`;
CREATE TABLE `tb_categorycomstru`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `email` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话，座机或手机',
  `remark` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '公司组织架构分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_categorycomstru
-- ----------------------------
INSERT INTO `tb_categorycomstru` VALUES (0, 'root', NULL, NULL, NULL);
INSERT INTO `tb_categorycomstru` VALUES (1, '产品技术中心', NULL, NULL, NULL);
INSERT INTO `tb_categorycomstru` VALUES (2, 'IT中心', NULL, NULL, NULL);
INSERT INTO `tb_categorycomstru` VALUES (3, '信息安全部', NULL, NULL, NULL);
INSERT INTO `tb_categorycomstru` VALUES (4, '运维部', NULL, NULL, NULL);
INSERT INTO `tb_categorycomstru` VALUES (5, 'it', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for tb_categorysecond
-- ----------------------------
DROP TABLE IF EXISTS `tb_categorysecond`;
CREATE TABLE `tb_categorysecond`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '漏洞二级分类编号',
  `categorytopid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞一级分类编号',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞二级分类类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '漏洞二级分类，用于记录二级漏洞类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_categorysecond
-- ----------------------------
INSERT INTO `tb_categorysecond` VALUES ('1252508920921067520', '1252508920883318784', '﻿代码执行');
INSERT INTO `tb_categorysecond` VALUES ('1252508920992370688', '1252508920954621952', 'SQL注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921013342208', '1252508920954621952', 'LDAP注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921055285248', '1252508920954621952', 'XPath注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921080451072', '1252508920954621952', 'NoSQL查询语句注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921122394112', '1252508920954621952', 'OS命令注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921143365632', '1252508920954621952', 'XML解析器注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921168531456', '1252508920954621952', '表达式语言(EL)注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921202085888', '1252508920954621952', 'OGNL注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921223057408', '1252508920954621952', 'SMTP注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921256611840', '1252508920954621952', 'IMAP注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921273389056', '1252508920954621952', 'ORM注入');
INSERT INTO `tb_categorysecond` VALUES ('1252508921290166272', '1252508920954621952', 'XXE');
INSERT INTO `tb_categorysecond` VALUES ('1252508921323720704', '1252508920954621952', 'HPP');
INSERT INTO `tb_categorysecond` VALUES ('1252508921344692224', '1252508920954621952', 'SSI');
INSERT INTO `tb_categorysecond` VALUES ('1252508921403412480', '1252508921361469440', '密码自动填充');
INSERT INTO `tb_categorysecond` VALUES ('1252508921424384000', '1252508921361469440', '用户枚举');
INSERT INTO `tb_categorysecond` VALUES ('1252508921449549824', '1252508921361469440', '暴力破解');
INSERT INTO `tb_categorysecond` VALUES ('1252508921470521344', '1252508921361469440', '默认密码');
INSERT INTO `tb_categorysecond` VALUES ('1252508921487298560', '1252508921361469440', '弱密码');
INSERT INTO `tb_categorysecond` VALUES ('1252508921504075776', '1252508921361469440', '弱密保');
INSERT INTO `tb_categorysecond` VALUES ('1252508921533435904', '1252508921361469440', '缺少多因素认证');
INSERT INTO `tb_categorysecond` VALUES ('1252508921546018816', '1252508921361469440', '失效的多因素认证');
INSERT INTO `tb_categorysecond` VALUES ('1252508921562796032', '1252508921361469440', '会话固定');
INSERT INTO `tb_categorysecond` VALUES ('1252508921587961856', '1252508921361469440', '会话保持');
INSERT INTO `tb_categorysecond` VALUES ('1252508921608933376', '1252508921361469440', '会话劫持');
INSERT INTO `tb_categorysecond` VALUES ('1252508921625710592', '1252508921361469440', '未限制登录失败次数');
INSERT INTO `tb_categorysecond` VALUES ('1252508921642487808', '1252508921361469440', '会话ID暴露在URL中');
INSERT INTO `tb_categorysecond` VALUES ('1252508921692819456', '1252508921671847936', '密码泄露');
INSERT INTO `tb_categorysecond` VALUES ('1252508921709596672', '1252508921671847936', '财务数据');
INSERT INTO `tb_categorysecond` VALUES ('1252508921738956800', '1252508921671847936', '医疗数据');
INSERT INTO `tb_categorysecond` VALUES ('1252508921764122624', '1252508921671847936', 'PII数据');
INSERT INTO `tb_categorysecond` VALUES ('1252508921818648576', '1252508921780899840', '未授权访问');
INSERT INTO `tb_categorysecond` VALUES ('1252508921873174528', '1252508921839620096', '不安全的默认配置');
INSERT INTO `tb_categorysecond` VALUES ('1252508921894146048', '1252508921839620096', '不必要的服务');
INSERT INTO `tb_categorysecond` VALUES ('1252508921910923264', '1252508921839620096', '不必要的功能');
INSERT INTO `tb_categorysecond` VALUES ('1252508921931894784', '1252508921839620096', '测试页面');
INSERT INTO `tb_categorysecond` VALUES ('1252508921965449216', '1252508921839620096', '错误处理机制向用户披露堆栈跟踪');
INSERT INTO `tb_categorysecond` VALUES ('1252508921982226432', '1252508921839620096', '错误处理机制向用户披露其他大量错误信息');
INSERT INTO `tb_categorysecond` VALUES ('1252508922011586560', '1252508921839620096', '应用程序服务器不安全配置');
INSERT INTO `tb_categorysecond` VALUES ('1252508922032558080', '1252508921839620096', '框架不安全配置');
INSERT INTO `tb_categorysecond` VALUES ('1252508922049335296', '1252508921839620096', '库文件不安全配置');
INSERT INTO `tb_categorysecond` VALUES ('1252508922061918208', '1252508921839620096', '数据库不安全配置');
INSERT INTO `tb_categorysecond` VALUES ('1252508922103861248', '1252508921839620096', '错误的HTTP头配置');
INSERT INTO `tb_categorysecond` VALUES ('1252508922120638464', '1252508921839620096', '目录列举');
INSERT INTO `tb_categorysecond` VALUES ('1252508922154192896', '1252508921839620096', '明文存储密码');
INSERT INTO `tb_categorysecond` VALUES ('1252508922175164416', '1252508921839620096', '弱hash存储密码');
INSERT INTO `tb_categorysecond` VALUES ('1252508922196135936', '1252508921839620096', '非最新/不受支持的版本');
INSERT INTO `tb_categorysecond` VALUES ('1252508922242273280', '1252508922225496064', '反射型XSS');
INSERT INTO `tb_categorysecond` VALUES ('1252508922259050496', '1252508922225496064', '存储型XSS');
INSERT INTO `tb_categorysecond` VALUES ('1252508922271633408', '1252508922225496064', 'DOM Based XSS');
INSERT INTO `tb_categorysecond` VALUES ('1252508922305187840', '1252508922292604928', '跨站请求伪造（CSRF）');
INSERT INTO `tb_categorysecond` VALUES ('1252508922334547968', '1252508922321965056', '服务端请求伪造（SSRF）');
INSERT INTO `tb_categorysecond` VALUES ('1252508922376491008', '1252508922355519488', '水平越权');
INSERT INTO `tb_categorysecond` VALUES ('1252508922389073920', '1252508922355519488', '垂直越权');
INSERT INTO `tb_categorysecond` VALUES ('1252508922431016960', '1252508922410045440', '不安全的反序列化');
INSERT INTO `tb_categorysecond` VALUES ('1252508922514903040', '1252508922498125824', '使用含有已知漏洞的组件');
INSERT INTO `tb_categorysecond` VALUES ('1252508922544263168', '1252508922531680256', '任意文件上传');
INSERT INTO `tb_categorysecond` VALUES ('1252508922573623296', '1252508922561040384', '文件包含');
INSERT INTO `tb_categorysecond` VALUES ('1252508922598789120', '1252508922586206208', '数据类型不严格校验');
INSERT INTO `tb_categorysecond` VALUES ('1252508922607177728', '1252508922586206208', '数据有效性不严格校验');
INSERT INTO `tb_categorysecond` VALUES ('1252508922623954944', '1252508922586206208', '数据格式不严格校验');
INSERT INTO `tb_categorysecond` VALUES ('1252508922653315072', '1252508922640732160', '密码找回逻辑');
INSERT INTO `tb_categorysecond` VALUES ('1252508922670092288', '1252508922640732160', '短信轰炸');
INSERT INTO `tb_categorysecond` VALUES ('1252508922682675200', '1252508922640732160', '商品金额篡改');
INSERT INTO `tb_categorysecond` VALUES ('1252508922733006848', '1252508922640732160', '商品数量篡改');
INSERT INTO `tb_categorysecond` VALUES ('1252508922753978368', '1252508922640732160', '0.01支付订单');
INSERT INTO `tb_categorysecond` VALUES ('1252508922779144192', '1252508922640732160', '负数充值');
INSERT INTO `tb_categorysecond` VALUES ('1252508922804310016', '1252508922640732160', '无限抽奖');
INSERT INTO `tb_categorysecond` VALUES ('1252508922821087232', '1252508922640732160', '批量注册');
INSERT INTO `tb_categorysecond` VALUES ('1252508922833670144', '1252508922640732160', '短信验证码包含在响应中');
INSERT INTO `tb_categorysecond` VALUES ('1252508922846253056', '1252508922640732160', '邮箱验证码包含在响应中');
INSERT INTO `tb_categorysecond` VALUES ('1252508922875613184', '1252508922858835968', '默认');
INSERT INTO `tb_categorysecond` VALUES ('1260829552448507904', '1260833385614544896', '拒绝服务');

-- ----------------------------
-- Table structure for tb_categorytab
-- ----------------------------
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
DROP TABLE IF EXISTS `tb_categorytab`;
CREATE TABLE `tb_categorytab`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `description` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '说明',
  `createtime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_categorytab
-- ----------------------------
INSERT INTO `tb_categorytab` VALUES (0, 'root', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (1, '应用系统等级', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (2, '非常重要', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (3, '重要', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (4, '一般', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (5, '应用系统访问策略', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (6, '需要SSO登录', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (7, '需要账号密码访问', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (8, '可外网访问', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (9, '云厂商', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (10, '阿里云', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (11, '腾讯云', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (12, '华为云', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (13, '机房', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (14, '北京机房', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (15, '上海机房', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (16, '杭州机房', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (17, '广州机房', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (18, '中间件分类', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (19, '数据库', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (20, '消息队列', NULL, NULL, NULL);
INSERT INTO `tb_categorytab` VALUES (21, '负载均衡', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for tb_categorytop
-- ----------------------------
DROP TABLE IF EXISTS `tb_categorytop`;
CREATE TABLE `tb_categorytop`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '漏洞一级分类编号',
  `name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞一级分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '漏洞一级分类，用于记录漏洞一级分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_categorytop
-- ----------------------------
INSERT INTO `tb_categorytop` VALUES ('1252508920883318784', '﻿代码执行');
INSERT INTO `tb_categorytop` VALUES ('1252508920954621952', '注入');
INSERT INTO `tb_categorytop` VALUES ('1252508921361469440', '失效的身份认证');
INSERT INTO `tb_categorytop` VALUES ('1252508921671847936', '敏感信息泄露');
INSERT INTO `tb_categorytop` VALUES ('1252508921780899840', '未授权访问');
INSERT INTO `tb_categorytop` VALUES ('1252508921839620096', '不安全的配置');
INSERT INTO `tb_categorytop` VALUES ('1252508922225496064', '跨站脚本攻击（XSS）');
INSERT INTO `tb_categorytop` VALUES ('1252508922292604928', '跨站请求伪造（CSRF）');
INSERT INTO `tb_categorytop` VALUES ('1252508922321965056', '服务端请求伪造（SSRF）');
INSERT INTO `tb_categorytop` VALUES ('1252508922355519488', '越权');
INSERT INTO `tb_categorytop` VALUES ('1252508922410045440', '不安全的反序列化');
INSERT INTO `tb_categorytop` VALUES ('1252508922498125824', '使用含有已知漏洞的组件');
INSERT INTO `tb_categorytop` VALUES ('1252508922531680256', '任意文件上传');
INSERT INTO `tb_categorytop` VALUES ('1252508922561040384', '文件包含');
INSERT INTO `tb_categorytop` VALUES ('1252508922586206208', '不严格的数据合法性校验');
INSERT INTO `tb_categorytop` VALUES ('1252508922640732160', '业务逻辑');
INSERT INTO `tb_categorytop` VALUES ('1252508922858835968', '默认');
INSERT INTO `tb_categorytop` VALUES ('1260833385614544896', '拒绝服务');

-- ----------------------------
-- Table structure for tb_categorytreecomstru
-- ----------------------------
DROP TABLE IF EXISTS `tb_categorytreecomstru`;
CREATE TABLE `tb_categorytreecomstru`  (
  `ancestor` bigint NOT NULL COMMENT '祖先',
  `descendant` bigint NOT NULL COMMENT '子代',
  `distance` bigint NOT NULL COMMENT '距离',
  PRIMARY KEY (`descendant`, `ancestor`, `distance`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '公司组织架构分类树表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_categorytreecomstru
-- ----------------------------
INSERT INTO `tb_categorytreecomstru` VALUES (0, 0, 0);
INSERT INTO `tb_categorytreecomstru` VALUES (0, 1, 1);
INSERT INTO `tb_categorytreecomstru` VALUES (1, 1, 0);
INSERT INTO `tb_categorytreecomstru` VALUES (0, 2, 1);
INSERT INTO `tb_categorytreecomstru` VALUES (2, 2, 0);
INSERT INTO `tb_categorytreecomstru` VALUES (0, 3, 2);
INSERT INTO `tb_categorytreecomstru` VALUES (1, 3, 1);
INSERT INTO `tb_categorytreecomstru` VALUES (3, 3, 0);
INSERT INTO `tb_categorytreecomstru` VALUES (0, 4, 2);
INSERT INTO `tb_categorytreecomstru` VALUES (1, 4, 1);
INSERT INTO `tb_categorytreecomstru` VALUES (4, 4, 0);
INSERT INTO `tb_categorytreecomstru` VALUES (0, 5, 2);
INSERT INTO `tb_categorytreecomstru` VALUES (2, 5, 1);
INSERT INTO `tb_categorytreecomstru` VALUES (5, 5, 0);

-- ----------------------------
-- Table structure for tb_categorytreetab
-- ----------------------------
DROP TABLE IF EXISTS `tb_categorytreetab`;
CREATE TABLE `tb_categorytreetab`  (
  `ancestor` bigint NOT NULL COMMENT '祖先',
  `descendant` bigint NOT NULL COMMENT '子代',
  `distance` bigint NOT NULL COMMENT '距离',
  PRIMARY KEY (`descendant`, `ancestor`, `distance`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签分类树表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_categorytreetab
-- ----------------------------
INSERT INTO `tb_categorytreetab` VALUES (0, 0, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 1, 1);
INSERT INTO `tb_categorytreetab` VALUES (1, 1, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 2, 2);
INSERT INTO `tb_categorytreetab` VALUES (1, 2, 1);
INSERT INTO `tb_categorytreetab` VALUES (2, 2, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 3, 2);
INSERT INTO `tb_categorytreetab` VALUES (1, 3, 1);
INSERT INTO `tb_categorytreetab` VALUES (3, 3, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 4, 2);
INSERT INTO `tb_categorytreetab` VALUES (1, 4, 1);
INSERT INTO `tb_categorytreetab` VALUES (4, 4, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 5, 1);
INSERT INTO `tb_categorytreetab` VALUES (5, 5, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 6, 2);
INSERT INTO `tb_categorytreetab` VALUES (5, 6, 1);
INSERT INTO `tb_categorytreetab` VALUES (6, 6, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 7, 2);
INSERT INTO `tb_categorytreetab` VALUES (5, 7, 1);
INSERT INTO `tb_categorytreetab` VALUES (7, 7, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 8, 2);
INSERT INTO `tb_categorytreetab` VALUES (5, 8, 1);
INSERT INTO `tb_categorytreetab` VALUES (8, 8, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 9, 1);
INSERT INTO `tb_categorytreetab` VALUES (9, 9, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 10, 2);
INSERT INTO `tb_categorytreetab` VALUES (9, 10, 1);
INSERT INTO `tb_categorytreetab` VALUES (10, 10, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 11, 2);
INSERT INTO `tb_categorytreetab` VALUES (9, 11, 1);
INSERT INTO `tb_categorytreetab` VALUES (11, 11, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 12, 2);
INSERT INTO `tb_categorytreetab` VALUES (9, 12, 1);
INSERT INTO `tb_categorytreetab` VALUES (12, 12, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 13, 1);
INSERT INTO `tb_categorytreetab` VALUES (13, 13, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 14, 2);
INSERT INTO `tb_categorytreetab` VALUES (13, 14, 1);
INSERT INTO `tb_categorytreetab` VALUES (14, 14, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 15, 2);
INSERT INTO `tb_categorytreetab` VALUES (13, 15, 1);
INSERT INTO `tb_categorytreetab` VALUES (15, 15, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 16, 2);
INSERT INTO `tb_categorytreetab` VALUES (13, 16, 1);
INSERT INTO `tb_categorytreetab` VALUES (16, 16, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 17, 2);
INSERT INTO `tb_categorytreetab` VALUES (13, 17, 1);
INSERT INTO `tb_categorytreetab` VALUES (17, 17, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 18, 1);
INSERT INTO `tb_categorytreetab` VALUES (18, 18, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 19, 2);
INSERT INTO `tb_categorytreetab` VALUES (18, 19, 1);
INSERT INTO `tb_categorytreetab` VALUES (19, 19, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 20, 2);
INSERT INTO `tb_categorytreetab` VALUES (18, 20, 1);
INSERT INTO `tb_categorytreetab` VALUES (20, 20, 0);
INSERT INTO `tb_categorytreetab` VALUES (0, 21, 2);
INSERT INTO `tb_categorytreetab` VALUES (18, 21, 1);
INSERT INTO `tb_categorytreetab` VALUES (21, 21, 0);

-- ----------------------------
-- Table structure for tb_checkresult
-- ----------------------------
DROP TABLE IF EXISTS `tb_checkresult`;
CREATE TABLE `tb_checkresult`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '检测结果编号',
  `assetportid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口编号',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测结果名称',
  `result` varchar(20480) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测结果',
  `risk` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缺陷风险级别',
  `activetime` datetime(0) NULL DEFAULT NULL COMMENT '缺陷发现时间',
  `passivetime` datetime(0) NULL DEFAULT NULL COMMENT '缺陷修复时间',
  `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '检测结果，用于记录nse或自定义插件检测结果' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_checkresult
-- ----------------------------

-- ----------------------------
-- Table structure for tb_checkresult_vuln
-- ----------------------------
DROP TABLE IF EXISTS `tb_checkresult_vuln`;
CREATE TABLE `tb_checkresult_vuln`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `checkresultid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '检测结果编号',
  `vulnid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '漏洞编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '检测结果漏洞中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_checkresult_vuln
-- ----------------------------

-- ----------------------------
-- Table structure for tb_contact
-- ----------------------------
DROP TABLE IF EXISTS `tb_contact`;
CREATE TABLE `tb_contact`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `email` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话，座机或手机',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '联系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_contact
-- ----------------------------

-- ----------------------------
-- Table structure for tb_contact_projectinfo
-- ----------------------------
DROP TABLE IF EXISTS `tb_contact_projectinfo`;
CREATE TABLE `tb_contact_projectinfo`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `contactid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系编号',
  `projectinfoid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目信息编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '联系项目信息中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_contact_projectinfo
-- ----------------------------

-- ----------------------------
-- Table structure for tb_cronjob
-- ----------------------------
DROP TABLE IF EXISTS `tb_cronjob`;
CREATE TABLE `tb_cronjob`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `cronexpression` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'cron表达式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '计划任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_cronjob
-- ----------------------------
INSERT INTO `tb_cronjob` VALUES ('1216569721126785024', '任务状态监控', '0/10 * * * * ?');
INSERT INTO `tb_cronjob` VALUES ('1216569761488572416', 'agent心跳包监控', '0 0/5 * * * ?');
INSERT INTO `tb_cronjob` VALUES ('1216569881969954816', '邮件资产报告', NULL);
INSERT INTO `tb_cronjob` VALUES ('1216569922503708672', '邮件漏洞报告', NULL);
INSERT INTO `tb_cronjob` VALUES ('1216569975163195392', '每天执行一次的任务', '0 21 3 * * ?');
INSERT INTO `tb_cronjob` VALUES ('1216570003462164488', '统计报表数据', '0 0 6 * * ?');
INSERT INTO `tb_cronjob` VALUES ('1342118487610494976', 'IM通知', '0/30 * * * * ?');

-- ----------------------------
-- Table structure for tb_democode
-- ----------------------------
DROP TABLE IF EXISTS `tb_democode`;
CREATE TABLE `tb_democode`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '漏洞示例代码编号',
  `vulnid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞编号',
  `democode` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '漏洞示例代码',
  `poc` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '漏洞poc',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '漏洞示例代码' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_democode
-- ----------------------------
INSERT INTO `tb_democode` VALUES ('1252508972309680128', '1252508972280320000', '默认信息漏洞示例代码', '默认信息漏洞POC');
INSERT INTO `tb_democode` VALUES ('1252508972506812416', '1252508972448092160', '默认低危漏洞示例代码', '默认低危漏洞POC');
INSERT INTO `tb_democode` VALUES ('1252508972615864320', '1252508972586504192', '默认中危漏洞示例代码', '默认中危漏洞POC');
INSERT INTO `tb_democode` VALUES ('1252508972695556096', '1252508972666195968', '默认高危漏洞示例代码', '默认高危漏洞POC');
INSERT INTO `tb_democode` VALUES ('1252508972771053568', '1252508972750082048', '默认严重漏洞示例代码', '默认严重漏洞POC');
INSERT INTO `tb_democode` VALUES ('1252508972859133952', '1252508972833968128', '默认致命漏洞示例代码', '默认致命漏洞POC');

-- ----------------------------
-- Table structure for tb_department
-- ----------------------------
DROP TABLE IF EXISTS `tb_department`;
CREATE TABLE `tb_department`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `departmentname` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '部门信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_department
-- ----------------------------

-- ----------------------------
-- Table structure for tb_dictionarypassword
-- ----------------------------
DROP TABLE IF EXISTS `tb_dictionarypassword`;
CREATE TABLE `tb_dictionarypassword`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字典编号',
  `password` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '字典，用于记录密码' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_dictionarypassword
-- ----------------------------
INSERT INTO `tb_dictionarypassword` VALUES ('1249945537030000640', 'root');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148513987072000', '1qaz@WSX');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514104512512', 'admin123456');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514171621376', '1qaz2wsx');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514238730240', 'root123456');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514276478976', 'root@123');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514314227712', 'root123');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514347782144', 'password');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514427473920', 'admin123!@#');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514465222656', '!QAZ@WSX');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514712686592', 'abc123!@#');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514763018240', 'P@ssw0rd');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514884653056', 'qwer1234');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148514989510656', 'admin123');
INSERT INTO `tb_dictionarypassword` VALUES ('1253148515132116992', 'root123!@#');
INSERT INTO `tb_dictionarypassword` VALUES ('1254965129133559808', 'test');
INSERT INTO `tb_dictionarypassword` VALUES ('1254965143540994048', 'postgres');
INSERT INTO `tb_dictionarypassword` VALUES ('1258954451188846592', '12345678');
INSERT INTO `tb_dictionarypassword` VALUES ('1259673102980354048', 'admin@123');
INSERT INTO `tb_dictionarypassword` VALUES ('1304068896218812416', 'admin');
INSERT INTO `tb_dictionarypassword` VALUES ('1304068896965398528', 'tomcat');
INSERT INTO `tb_dictionarypassword` VALUES ('1304068897175113728', 'administrator');
INSERT INTO `tb_dictionarypassword` VALUES ('1304068932222717952', 'toor');
INSERT INTO `tb_dictionarypassword` VALUES ('1304068956872642560', '123456');

-- ----------------------------
-- Table structure for tb_dictionaryusername
-- ----------------------------
DROP TABLE IF EXISTS `tb_dictionaryusername`;
CREATE TABLE `tb_dictionaryusername`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字典编号',
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典用户名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '字典，用于记录用户名' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_dictionaryusername
-- ----------------------------
INSERT INTO `tb_dictionaryusername` VALUES ('1304068407464955904', 'admin');
INSERT INTO `tb_dictionaryusername` VALUES ('1304068407913746432', 'root');
INSERT INTO `tb_dictionaryusername` VALUES ('1304068407976660992', 'tomcat');
INSERT INTO `tb_dictionaryusername` VALUES ('1304068408039575552', 'administrator');
INSERT INTO `tb_dictionaryusername` VALUES ('1304068408102490112', 'test');
INSERT INTO `tb_dictionaryusername` VALUES ('1305054577313320960', 'linux');

-- ----------------------------
-- Table structure for tb_domainwhitelist
-- ----------------------------
DROP TABLE IF EXISTS `tb_domainwhitelist`;
CREATE TABLE `tb_domainwhitelist`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '参数编号',
  `domain` varchar(254) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '域名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '域名白名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_domainwhitelist
-- ----------------------------
INSERT INTO `tb_domainwhitelist` VALUES ('1166176029530787868', 'microsoft.com');
INSERT INTO `tb_domainwhitelist` VALUES ('1212621047124332544', 'centos.org');
INSERT INTO `tb_domainwhitelist` VALUES ('1212633068138008576', 'apache.org');
INSERT INTO `tb_domainwhitelist` VALUES ('1212633375786012672', 'qq.com');
INSERT INTO `tb_domainwhitelist` VALUES ('1212634936557834240', 'github.com');
INSERT INTO `tb_domainwhitelist` VALUES ('1212636857746198528', 'nginx.net');
INSERT INTO `tb_domainwhitelist` VALUES ('1212985316789587968', 'baidu.com');
INSERT INTO `tb_domainwhitelist` VALUES ('1234145903191920640', 'google.com');
INSERT INTO `tb_domainwhitelist` VALUES ('1234147367637356544', 'firefox.com');

-- ----------------------------
-- Table structure for tb_host
-- ----------------------------
DROP TABLE IF EXISTS `tb_host`;
CREATE TABLE `tb_host`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主机编号',
  `assetipid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产ip编号',
  `macaddress` varchar(48) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'mac地址',
  `hostname` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ostype` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `osversion` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `owner` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `activetime` datetime(0) NULL DEFAULT NULL COMMENT '主机发现时间',
  `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `subdomain` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '子域名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '主机，用于记录主机信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_host
-- ----------------------------

-- ----------------------------
-- Table structure for tb_imvulnnotify
-- ----------------------------
DROP TABLE IF EXISTS `tb_imvulnnotify`;
CREATE TABLE `tb_imvulnnotify`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `dingtalknotify` tinyint(1) NULL DEFAULT NULL COMMENT '钉钉群机器人是否开启通知',
  `dingtalknotifyall` tinyint(1) NULL DEFAULT NULL COMMENT '钉钉群机器人是否提醒所有人',
  `secret` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '秘钥',
  `risk` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '风险等级',
  `dingtalkreceiver` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '钉钉群接收人列表',
  `dingtalkmessageurl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '钉钉群机器人消息地址',
  `messagetitle` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息标题',
  `messageprefix` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息前缀',
  `messagesuffix` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息后缀',
  `messagecharset` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息编码',
  `wechatnotify` tinyint(1) NULL DEFAULT NULL COMMENT '企微群机器人是否开启通知',
  `wechatnotifyall` tinyint(1) NULL DEFAULT NULL COMMENT '企微群机器人是否提醒所有人',
  `wechatmessageurl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企微群机器人消息地址',
  `wechatreceiver` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企微群接收人列表',
  `riskassetnotify` tinyint(1) NULL DEFAULT NULL COMMENT '新增高危资产实时推送到群',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '即时消息漏洞提醒表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_imvulnnotify
-- ----------------------------
INSERT INTO `tb_imvulnnotify` VALUES ('1143092349392524201', 0, 0, '', '中危,高危,严重,致命', '', '', '【魔方】实时通知', '您好，以下为【魔方】最新发现的高危资产/漏洞信息', '请及时处理，如需帮助，请联系信息安全部。', 'utf-8', 0, 0, '', NULL, 0);


-- ----------------------------
-- Table structure for tb_ipportwhitelist
-- ----------------------------
DROP TABLE IF EXISTS `tb_ipportwhitelist`;
CREATE TABLE `tb_ipportwhitelist`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `ipwhitelistid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip白名单编号',
  `port` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口',
  `checkwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '检测白名单',
  `notifywhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '提醒白名单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'ip端口白名单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_ipportwhitelist
-- ----------------------------

-- ----------------------------
-- Table structure for tb_ipwhitelist
-- ----------------------------
DROP TABLE IF EXISTS `tb_ipwhitelist`;
CREATE TABLE `tb_ipwhitelist`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `ip` varchar(39) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip',
  `checkwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '检测白名单',
  `notifywhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '提醒白名单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'ip白名单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_ipwhitelist
-- ----------------------------

-- ----------------------------
-- Table structure for tb_nmapconfig
-- ----------------------------
DROP TABLE IF EXISTS `tb_nmapconfig`;
CREATE TABLE `tb_nmapconfig`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'nmap配置编号',
  `taskid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务编号',
  `threadnumber` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '线程数量，在mass2Nmap模式下使用',
  `singleipscantime` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单个ip扫描次数，在mass2Nmap模式下使用',
  `additionoption` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附加选项，在mass2Nmap模式下使用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'mass2Nmap模式下，nmap的配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_nmapconfig
-- ----------------------------

-- ----------------------------
-- Table structure for tb_notifylog
-- ----------------------------
DROP TABLE IF EXISTS `tb_notifylog`;
CREATE TABLE `tb_notifylog`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `recipient` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收人',
  `receiveuser` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收账户',
  `content` varchar(20480) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容',
  `success` tinyint(1) NULL DEFAULT NULL COMMENT '发送成功',
  `exception` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常消息',
  `sendtime` datetime(0) NULL DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '通知记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_notifylog
-- ----------------------------

-- ----------------------------
-- Table structure for tb_pluginassetservice
-- ----------------------------
DROP TABLE IF EXISTS `tb_pluginassetservice`;
CREATE TABLE `tb_pluginassetservice`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产服务编号',
  `pluginconfigid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件配置编号',
  `assetservice` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产服务',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产服务，记录数据库中所有ip的service，根据服务确定对应的nse或者自定义插件进行扫描' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_pluginassetservice
-- ----------------------------
INSERT INTO `tb_pluginassetservice` VALUES ('1298634974131523584', '1298634973577875456', 'redis');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634975737942016', '1298634975670833152', 'memcached');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634975968628736', '1298634975670833152', 'memcache');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634976098652160', '1298634976069292032', 'zookeeper');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634976325144576', '1298634976283201536', 'mongodb');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634976639717376', '1298634976555831296', 'ssh');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634977168199680', '1298634977138839552', 'mysql');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634977453412352', '1298634977419857920', 'ftp');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634977776373760', '1298634977734430720', 'ms-sql-s');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634978103529472', '1298634977918980096', 'oracle-tns');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634978267107328', '1298634978246135808', 'postgresql');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634978426490880', '1298634978397130752', 'netbios-ssn');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634978569097216', '1298634978397130752', 'microsoft-ds');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634978715897856', '1298634978690732032', 'memcached');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634978875281408', '1298634978850115584', 'x11');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634978992721920', '1298634978967556096', 'mysql');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634979235991552', '1298634979105968128', 'ms-wbt-server');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634979558952960', '1298634979529592832', 'microsoft-ds');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634980020326400', '1298634979865137152', 'microsoft-ds');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634980347482112', '1298634980204875776', 'mysql');
INSERT INTO `tb_pluginassetservice` VALUES ('1298634980683026432', '1298634980557197312', 'redis');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635549208350720', '1298635549027995648', 'redis');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635549829107712', '1298635549791358976', 'memcached');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635549971714048', '1298635549791358976', 'memcache');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635550072377344', '1298635550038822912', 'zookeeper');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635550261121024', '1298635550223372288', 'mongodb');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635550496002048', '1298635550437281792', 'ssh');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635551120953344', '1298635551074816000', 'mysql');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635551552966656', '1298635551494246400', 'ftp');
INSERT INTO `tb_pluginassetservice` VALUES ('1298635668129452032', '1298635551737516032', 'http');

-- ----------------------------
-- Table structure for tb_pluginassetversion
-- ----------------------------
DROP TABLE IF EXISTS `tb_pluginassetversion`;
CREATE TABLE `tb_pluginassetversion`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产版本编号',
  `pluginconfigid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件配置编号',
  `assetversion` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产版本，记录数据库中所有ip的version，根据版本确定对应的nse或者自定义插件进行扫描' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_pluginassetversion
-- ----------------------------
INSERT INTO `tb_pluginassetversion` VALUES ('1298634974316072960', '1298634973577875456', 'Redis key-value store');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634975792467968', '1298634975670833152', 'Memcached');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634976157372416', '1298634976069292032', 'Zookeeper');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634976358699008', '1298634976283201536', 'MongoDB');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634976740380672', '1298634976555831296', 'ssh');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634977197559808', '1298634977138839552', 'mysql');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634977348554752', '1298634977138839552', 'MariaDB');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634977486966784', '1298634977419857920', 'ftp');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634977621184512', '1298634977591824384', 'nginx');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634977809928192', '1298634977734430720', 'SQL Server');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634978153861120', '1298634977918980096', 'Oracle');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634978296467456', '1298634978246135808', 'PostgreSQL');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634978451656704', '1298634978397130752', 'Samba');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634978602651648', '1298634978397130752', 'microsoft-ds');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634978749452288', '1298634978690732032', 'Memcached 1.4.5');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634979265351680', '1298634979105968128', 'Microsoft Terminal Service');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634980104212480', '1298634979865137152', 'Microsoft Windows Server 2008 R2 - 2012 microsoft-ds');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634980443951104', '1298634980204875776', 'MySQL 5.6.26');
INSERT INTO `tb_pluginassetversion` VALUES ('1298634980762718208', '1298634980557197312', 'Redis key-value store');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635549250293760', '1298635549027995648', 'Redis key-value store');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635549858467840', '1298635549791358976', 'Memcached');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635550110126080', '1298635550038822912', 'Zookeeper');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635550286286848', '1298635550223372288', 'MongoDB');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635550546333696', '1298635550437281792', 'ssh');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635551175479296', '1298635551074816000', 'mysql');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635551397777408', '1298635551074816000', 'MariaDB');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635551594909696', '1298635551494246400', 'ftp');
INSERT INTO `tb_pluginassetversion` VALUES ('1298635551771070464', '1298635551737516032', 'nginx');
INSERT INTO `tb_pluginassetversion` VALUES ('1305054771866112000', '1305054770993696768', 'Elasticsearch');

-- ----------------------------
-- Table structure for tb_pluginconfig
-- ----------------------------
DROP TABLE IF EXISTS `tb_pluginconfig`;
CREATE TABLE `tb_pluginconfig`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '插件配置编号',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '插件名称',
  `args` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件参数',
  `risk` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件风险级别',
  `type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件类型：nse或者自定义',
  `timeout` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件超时',
  `plugincode` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '插件代码',
  `validatetype` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'http辅助验证或dns辅助验证',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '插件配置，用于记录插件配置信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_pluginconfig
-- ----------------------------
INSERT INTO `tb_pluginconfig` VALUES ('1298634973577875456', 'JavaRedisWeakPass', 'info', '高危', 'selfd', '3000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634975670833152', 'JavaMemcachedUnauth', 'stats', '中危', 'selfd', '3000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634976069292032', 'JavaZookeeperUnauth', 'envi', '中危', 'selfd', '3000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634976283201536', 'JavaMongoDbUnauth', '3F0000007E00000000000000D40700000400000061646D696E2E24636D640000000000FFFFFFFF18000000106C697374446174616261736573000100000000', '中危', 'selfd', '3000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634976555831296', 'JavaSSHWeakPass', 'cat /etc/passwd', '严重', 'selfd', '3000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634977138839552', 'JavaMysqlWeakPass', 'mysql', '严重', 'selfd', '5000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634977419857920', 'JavaFTPWeakPass', '10', '中危', 'selfd', '3000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634977591824384', 'JavaNginxVerDetect', '', '低危', 'selfd', '3000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634977734430720', 'JavaMsSqlServerWeakPass', 'master', '严重', 'selfd', '5000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634977918980096', 'JavaOracleWeakPass', 'orcl,xe,test', '严重', 'selfd', '5000', '', NULL);
INSERT INTO `tb_pluginconfig` VALUES ('1298634978246135808', 'JavaPostgresSqlWeakPass', 'postgres', '严重', 'selfd', '5000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634978397130752', 'JavaSambaWeakPass', '', '致命', 'selfd', '5000', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634978690732032', 'memcached-info', '', '中危', 'nse', '0', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634978850115584', 'x11-access', '', '高危', 'nse', '0', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634978967556096', 'mysql-vuln-cve2012-2122', '', '严重', 'nse', '0', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634979105968128', 'rdp-vuln-ms12-020', '', '高危', 'nse', '0', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634979529592832', 'smb-vuln-ms08-067', '', '高危', 'nse', '0', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634979865137152', 'smb-vuln-ms17-010', '', '高危', 'nse', '0', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634980204875776', 'mysql-empty-password', '', '严重', 'nse', '0', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298634980557197312', 'redis-info', '', '高危', 'nse', '0', NULL, '');
INSERT INTO `tb_pluginconfig` VALUES ('1298635549027995648', 'RedisWeakPass', 'info', '高危', 'selfd', '3000', '\n# -*- coding:utf-8 -*-\nimport socket\n\n\ndef check(ip, port, args, timeout, payload_map):\n    password_list = payload_map.get(\'password\')\n\n    try:\n        socket.setdefaulttimeout(int(timeout))\n        for password in password_list:\n            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\n            s.connect((ip, int(port)))\n            s.send((\'AUTH \' + password + \'\\r\\n\' + args + \'\\r\\n\').encode())\n            byte_result = s.recv(1024)\n            str_result = str(byte_result)\n            if \'no password is set\' in str_result:\n                return \'Redis未设置密码\' + byte_result.decode()\n            if \'+OK\' in str_result:\n                return \'密码：\' + password + \'\\n\' + byte_result.decode()\n    except Exception:\n        raise\n', '');
INSERT INTO `tb_pluginconfig` VALUES ('1298635549791358976', 'MemcachedUnauth', 'stats', '中危', 'selfd', '3000', '\n# -*- coding:utf-8 -*-\nimport socket\n\n\ndef check(ip, port, args, timeout, payload_map):\n    try:\n        socket.setdefaulttimeout(int(timeout))\n        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\n        s.connect((ip, int(port)))\n        s.send((args + \'\\r\\n\').encode())\n        byte_result = s.recv(1024)\n        return \'memcached未授权访问\\n\' + byte_result.decode()\n    except Exception:\n        raise\n', '');
INSERT INTO `tb_pluginconfig` VALUES ('1298635550038822912', 'ZookeeperUnauth', 'envi', '中危', 'selfd', '3000', '\n# -*- coding:utf-8 -*-\nimport socket\n\n\ndef check(ip, port, args, timeout, payload_map):\n    try:\n        socket.setdefaulttimeout(int(timeout))\n        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\n        s.connect((ip, int(port)))\n        s.send((args + \'\\r\\n\').encode())\n        byte_result = s.recv(1024)\n        return \'Zookeeper未授权访问\\n\' + byte_result.decode()\n    except Exception:\n        raise\n', '');
INSERT INTO `tb_pluginconfig` VALUES ('1298635550223372288', 'MongoDbUnauth', '3F0000007E00000000000000D40700000400000061646D696E2E24636D640000000000FFFFFFFF18000000106C697374446174616261736573000100000000', '中危', 'selfd', '3000', '\n# -*- coding:utf-8 -*-\nimport socket\nimport binascii\n\n\ndef check(ip, port, args, timeout, payload_map):\n    try:\n        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\n        socket.setdefaulttimeout(int(timeout))\n        s.connect((ip, int(port)))\n        s.send(binascii.a2b_hex(args))\n        byte_result = s.recv(1024)\n        str_result = str(byte_result)\n        if \'errmsg\' not in str_result:\n            return \'MongoDb未授权访问\\n\' + str_result\n    except Exception:\n        raise\n', '');
INSERT INTO `tb_pluginconfig` VALUES ('1298635550437281792', 'SSHWeakPass', 'cat /etc/passwd', '严重', 'selfd', '3000', '\n# -*- coding:utf-8 -*-\nfrom pexpect import pxssh\n\n\ndef check(ip, port, args, timeout, payload_map):\n    username_list = payload_map.get(\'username\')\n    password_list = payload_map.get(\'password\')\n    username_flag = False\n\n    try:\n        for username in username_list:\n            for password in password_list:\n                try:\n                    ssh = pxssh.pxssh()\n                    ssh.login(server=ip, port=port, username=username, password=password, login_timeout=int(timeout))\n                    ssh.sendline(args)\n                    ssh.prompt()\n                    result = \"用户名密码: \" + username + \":\" + password\n                    result = result + ssh.before.decode().replace(args, \'\')\n                    ssh.logout()\n                    return result\n                except pxssh.ExceptionPxssh:\n                    pass\n    except Exception:\n        raise\n', '');
INSERT INTO `tb_pluginconfig` VALUES ('1298635551074816000', 'MysqlWeakPass', 'mysql', '严重', 'selfd', '5000', '\n# -*- coding:utf-8 -*-\nimport pymysql\n\n\ndef check(ip, port, args, timeout, payload_map):\n    username_list = payload_map.get(\'username\')\n    password_list = payload_map.get(\'password\')\n\n    try:\n        for username in username_list:\n            for password in password_list:\n                try:\n                    conn = pymysql.connect(host=ip, port=int(port), user=username, passwd=password, db=args,\n                                           charset=\'utf8\')\n                    result = conn.db\n                    conn.close()\n                    return \'用户名密码: \' + username + \':\' + password + \'\\n\' + result.decode()\n                except Exception as e:\n                    pass\n    except Exception:\n        raise\n', '');
INSERT INTO `tb_pluginconfig` VALUES ('1298635551494246400', 'FTPWeakPass', '10', '中危', 'selfd', '3000', '\n# -*- coding:utf-8 -*-\nimport ftplib\n\n\ndef check(ip, port, args, timeout, payload_map):\n    username_list = payload_map.get(\'username\')\n    password_list = payload_map.get(\'password\')\n\n    try:\n        ftp = ftplib.FTP()\n        ftp.timeout = int(timeout)\n        for username in username_list:\n            for password in password_list:\n                try:\n                    ftp.connect(ip, int(port))\n                    ftp.login(username, password)\n                    if username == \'ftp\':\n                        result = \"FTP允许匿名访问\"\n                    else:\n                        result = \"用户名密码: \" + username + \":\" + password\n                    return result\n                except ftplib.error_perm:\n                    pass\n        ftp.quit()\n    except Exception:\n        raise\n', '');
INSERT INTO `tb_pluginconfig` VALUES ('1298635551737516032', 'NginxVerDetect', '', '低危', 'selfd', '3000', '\n# -*- coding:utf-8 -*-\n\n\ndef check(ip, port, args, timeout, payload_map):\n    nginx_mainline_version = payload_map.get(\'Mainline version\')[0]\n    nginx_stable_version = payload_map.get(\'Stable version\')[0]\n    nginx_raw_version = payload_map.get(\'rawVersion\')[0]\n    split_version = nginx_raw_version.split(\' \')\n    if len(split_version) == 2:\n        real_version = nginx_raw_version.split(\' \')[1]\n        if real_version is not None:\n            real_cp_mainline = compare_version(nginx_mainline_version, real_version)\n            real_cp_stable = compare_version(nginx_stable_version, real_version)\n            if real_cp_mainline == 0 or real_cp_stable == 0:\n                return nginx_raw_version + \' is up to date\'\n            else:\n                # 小于主线版本\n                if real_cp_mainline == 1:\n                    return nginx_raw_version + \' is out of date. \' + \'Mainline version: \' + nginx_mainline_version\n                # 小于稳定版\n                if real_cp_stable == 1:\n                    return nginx_raw_version + \' is out of date. \' + \'Stable version: \' + nginx_stable_version\n\n\n# 0相等，1左边大，-1右边大\n# version1----第一个要比较的版本字符串\n# version2----第二个要比较的版本字符串\n# split_flag----版本分隔符，默认为\".\"，可自定义\n# 接受的版本字符形式----空/x/x.y/x.y./x.y.z；两个参数可为前边列出的形式的任一种\ndef compare_version(version1=None, version2=None, split_flag=\".\"):\n    # 如果存在有为空的情况则进入\n    if (version1 is None) or (version1 == \"\") or (version2 is None) or (version2 == \"\"):\n        # version1为空且version2不为空，则返回version2大\n        if ((version1 is None) or (version1 == \"\")) and (version2 is not None) and (version2 != \"\"):\n            return -1\n        # version2为空且version1不为空，则返回version1大\n        if ((version2 is None) or (version2 == \"\")) and (version1 is not None) and (version1 != \"\"):\n            return 1\n\n    # 如果版本字符串相等，那么直接返回相等，这句会且只会在第一次比较时才可能进入\n    # version1和version2都为空时也会进入这里\n    if version1 == version2:\n        return 0\n\n    # 对版本字符串从左向右查找\".\"，第一个\".\"之前的字符串即为此次要比较的版本\n    # 如1.3.5中的1\n    try:\n        current_section_version1 = version1[:version1.index(split_flag)]\n    except:\n        current_section_version1 = version1\n    try:\n        current_section_version2 = version2[:version2.index(split_flag)]\n    except:\n        current_section_version2 = version2\n    # 对本次要比较的版本字符转成整型进行比较\n    if int(current_section_version1) > int(current_section_version2):\n        return 1\n    elif int(current_section_version1) < int(current_section_version2):\n        return -1\n\n    # 如果本次传来版本字符串中已没有版本号分隔符，那说明本次比较的版本号已是最后一位版本号，下次比较值赋空\n    # 如本次传来的是5，那下次要比较的只能赋空\n    try:\n        other_section_version1 = version1[version1.index(split_flag) + 1:]\n    except:\n        other_section_version1 = \"\"\n    try:\n        other_section_version2 = version2[version2.index(split_flag) + 1:]\n    except:\n        other_section_version2 = \"\"\n\n    # 递归调用比较\n    return compare_version(other_section_version1, other_section_version2)\n', '');
INSERT INTO `tb_pluginconfig` VALUES ('1305054770993696768', 'HTTPElasticsearchUnauth', '', '高危', 'selfd', '0', '\n{\n    \"protocol\":\"http\",\n    \"method\":\"get\", \n    \"url\":\"/_cat\"\n}\n', '');

-- ----------------------------
-- Table structure for tb_project
-- ----------------------------
DROP TABLE IF EXISTS `tb_project`;
CREATE TABLE `tb_project`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '项目编号',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '项目名称',
  `description` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '项目，用于记录项目信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_project
-- ----------------------------

-- ----------------------------
-- Table structure for tb_projectinfo
-- ----------------------------
DROP TABLE IF EXISTS `tb_projectinfo`;
CREATE TABLE `tb_projectinfo`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `departmentid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门编号',
  `projectname` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目名称',
  `checkwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '检测白名单',
  `notifywhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '提醒白名单',
  `inserttime` datetime(0) NULL DEFAULT NULL COMMENT '插入时间',
  `overrideipwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '覆盖ip白名单，默认为false，如果为true，则会对项目下所有的ip进行白名单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '项目信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_projectinfo
-- ----------------------------

-- ----------------------------
-- Table structure for tb_projectportwhitelist
-- ----------------------------
DROP TABLE IF EXISTS `tb_projectportwhitelist`;
CREATE TABLE `tb_projectportwhitelist`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `projectinfoid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目信息编号',
  `port` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口',
  `checkwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '检测白名单',
  `notifywhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '提醒白名单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '项目端口白名单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_projectportwhitelist
-- ----------------------------

-- ----------------------------
-- Table structure for tb_projectvulnnotify
-- ----------------------------
DROP TABLE IF EXISTS `tb_projectvulnnotify`;
CREATE TABLE `tb_projectvulnnotify`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `risk` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '风险等级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '项目组漏洞提醒' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_projectvulnnotify
-- ----------------------------
INSERT INTO `tb_projectvulnnotify` VALUES ('1143092349392523271', '低危,中危,高危,严重,致命');

-- ----------------------------
-- Table structure for tb_riskport
-- ----------------------------
DROP TABLE IF EXISTS `tb_riskport`;
CREATE TABLE `tb_riskport`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `port` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高危端口',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '高危端口表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_riskport
-- ----------------------------
INSERT INTO `tb_riskport` VALUES ('1168817979845120001', '6379');
INSERT INTO `tb_riskport` VALUES ('1233467415178907648', '21');
INSERT INTO `tb_riskport` VALUES ('1233468251812532224', '22');
INSERT INTO `tb_riskport` VALUES ('1233468309136084992', '3306');
INSERT INTO `tb_riskport` VALUES ('1233468375011823616', '11211');
INSERT INTO `tb_riskport` VALUES ('1233468459753541632', '137');
INSERT INTO `tb_riskport` VALUES ('1233468479894589440', '139');
INSERT INTO `tb_riskport` VALUES ('1233468525310513152', '445');
INSERT INTO `tb_riskport` VALUES ('1233468610551353344', '3389');
INSERT INTO `tb_riskport` VALUES ('1233468635159334912', '1433');
INSERT INTO `tb_riskport` VALUES ('1233468655359102976', '1521');
INSERT INTO `tb_riskport` VALUES ('1233468701420949504', '161');
INSERT INTO `tb_riskport` VALUES ('1233468746341945344', '162');
INSERT INTO `tb_riskport` VALUES ('1233468788243042304', '23');
INSERT INTO `tb_riskport` VALUES ('1233471409758539776', '5432');

-- ----------------------------
-- Table structure for tb_riskservice
-- ----------------------------
DROP TABLE IF EXISTS `tb_riskservice`;
CREATE TABLE `tb_riskservice`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `service` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高危服务',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '高危服务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_riskservice
-- ----------------------------
INSERT INTO `tb_riskservice` VALUES ('1168817979845120011', 'redis');
INSERT INTO `tb_riskservice` VALUES ('1233467447277916160', 'ftp');
INSERT INTO `tb_riskservice` VALUES ('1233468896120541184', 'ssh');
INSERT INTO `tb_riskservice` VALUES ('1233469023258284032', 'telnet');
INSERT INTO `tb_riskservice` VALUES ('1233469102396411904', 'snmp');
INSERT INTO `tb_riskservice` VALUES ('1233469233241919488', 'mysql');
INSERT INTO `tb_riskservice` VALUES ('1233469416776273920', 'oracle-tns');
INSERT INTO `tb_riskservice` VALUES ('1233469517036916736', 'ms-sql-s');
INSERT INTO `tb_riskservice` VALUES ('1233469629473624064', 'ms-wbt-server');
INSERT INTO `tb_riskservice` VALUES ('1233469800768999424', 'microsoft-ds');
INSERT INTO `tb_riskservice` VALUES ('1233469905303638016', 'netbios-ssn');
INSERT INTO `tb_riskservice` VALUES ('1233470365368455168', 'memcached');
INSERT INTO `tb_riskservice` VALUES ('1233470924116856832', 'X11');
INSERT INTO `tb_riskservice` VALUES ('1233471342574178304', 'postgresql');

-- ----------------------------
-- Table structure for tb_riskversion
-- ----------------------------
DROP TABLE IF EXISTS `tb_riskversion`;
CREATE TABLE `tb_riskversion`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `version` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高危版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '高危版本表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_riskversion
-- ----------------------------
INSERT INTO `tb_riskversion` VALUES ('1168817979845120021', 'Redis key-value store');
INSERT INTO `tb_riskversion` VALUES ('1168817979845120022', 'Redis key-value store 5.0');
INSERT INTO `tb_riskversion` VALUES ('1233470404446785536', 'Memcached');

-- ----------------------------
-- Table structure for tb_sendmailconfig
-- ----------------------------
DROP TABLE IF EXISTS `tb_sendmailconfig`;
CREATE TABLE `tb_sendmailconfig`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `sendhost` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱host',
  `sendpassword` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `sendfrom` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发件人',
  `sendto` varchar(2018) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提醒邮箱，强制提醒，不管是否在提醒白名单里，提醒包括所有资产和在收件人列表接收漏洞风险中的漏洞',
  `sendtorisk` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人列表接收漏洞风险',
  `vulnsubject` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞邮件主题',
  `assetsubject` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产邮件主题',
  `vulncontent` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞邮件内容',
  `assetcontent` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产邮件内容',
  `excelauthor` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'excel作者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '发邮件配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_sendmailconfig
-- ----------------------------
INSERT INTO `tb_sendmailconfig` VALUES ('1143092349392523272', NULL, NULL, '', '', '低危,中危,高危,严重,致命', '【魔方】漏洞报告', '【魔方】资产报告', '您好，附件为漏洞报告', '您好，附件为资产报告', '信息安全部');

-- ----------------------------
-- Table structure for tb_solution
-- ----------------------------
DROP TABLE IF EXISTS `tb_solution`;
CREATE TABLE `tb_solution`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '修复方案编号',
  `vulnid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞编号',
  `solution` varchar(10240) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修复方案',
  `codedemo` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '修复代码示例',
  `configdemo` varchar(10240) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修复配置示例',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '修复方案，用于记录漏洞修复方案' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_solution
-- ----------------------------
INSERT INTO `tb_solution` VALUES ('1252508972364206080', '1252508972280320000', '默认信息修复方案', '默认信息修复代码示例', '默认信息修复配置示例');
INSERT INTO `tb_solution` VALUES ('1252508972527783936', '1252508972448092160', '默认低危修复方案', '默认低危修复代码示例', '默认低危修复配置示例');
INSERT INTO `tb_solution` VALUES ('1252508972641030144', '1252508972586504192', '默认中危修复方案', '默认中危修复代码示例', '默认中危修复配置示例');
INSERT INTO `tb_solution` VALUES ('1252508972716527616', '1252508972666195968', '默认高危修复方案', '默认高危修复代码示例', '默认高危修复配置示例');
INSERT INTO `tb_solution` VALUES ('1252508972787830784', '1252508972750082048', '默认严重修复方案', '默认严重修复代码示例', '默认严重修复配置示例');
INSERT INTO `tb_solution` VALUES ('1252508972875911168', '1252508972833968128', '默认致命修复方案', '默认致命修复代码示例', '默认致命修复配置示例');
INSERT INTO `tb_solution` VALUES ('1260513758107799552', '1260513758078439424', '1.设置redis只对本地开放，2.添加密码，3.配置防火墙，只对业务ip开放redis访问权限', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758191685632', '1260513758166519808', '1.设置Memcache只对本地开放，2.编译时加上–enable-sasl，启用SASL认证，3.配置防火墙，只对业务ip开放Memcache访问权限', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758262988800', '1260513758237822976', '1.添加密码，2.配置防火墙，只对业务ip开放ftp访问权限', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758422372352', '1260513758384623616', '1.修改ssh密码，使密码符合公司密码复杂度要求，2.配置防火墙，只对业务ip开放ssh访问权限', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758506258432', '1260513758468509696', '1.本地监听,如MongoDB只需在本地使用，建议只在本地开启监听服务，使用--bind_ip 127.0.0.1绑定监听地址，2.限制访问源,如果仅对内网服务器提供服务，建议禁止将MongoDB服务发布到互联网上，并在主机上通过防火墙限制访问源IP，3.启动基于角色的登录认证功能,MongoDB支持SCRAM、x.509证书认证等多种认证机制，SCRAM（Salted Challenge Response Authentication Mechanism）是3.x版本的默认认证机制，该机制通过用户名、密码验证，基于用户角色进行访问控制', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758598533120', '1260513758560784384', '1.禁止把Zookeeper直接暴露在公网，2.添加访问控制，根据情况选择对应方式（认证用户，用户名密码，指定IP）', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758682419200', '1260513758653059072', '1.MySQL如果不需要外部访问，设置只对本地开放，2.对未设置密码的用户添加密码，3.权限最小化, 为不同的数据库用户赋予不同的权限，4.配置防火墙，只对业务ip开放MySQL访问权限', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758745333760', '1260513758720167936', '1.建议将Nginx版本更新到最新主线版或最新稳定版，2.请前往官网http://nginx.org下载最新主线版本或稳定版进行更新', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758808248320', '1260513758787276800', '1.MS SQL Server如果不需要外部访问，设置只对本地开放，2.对未设置密码的用户添加密码，3.权限最小化, 为不同的数据库用户赋予不同的权限，4.配置防火墙，只对业务ip开放MS SQL Server访问权限', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758871162880', '1260513758854385664', '1.Oracle如果不需要外部访问，设置只对本地开放，2.对未设置密码的用户添加密码，3.权限最小化, 为不同的数据库用户赋予不同的权限，4.配置防火墙，只对业务ip开放Oracle访问权限', '', '');
INSERT INTO `tb_solution` VALUES ('1260513758942466048', '1260513758921494528', '1.PostgreSQL如果不需要外部访问，设置只对本地开放，2.对未设置密码的用户添加密码，3.权限最小化, 为不同的数据库用户赋予不同的权限，4.配置防火墙，只对业务ip开放PostgreSQL访问权限', '', '');
INSERT INTO `tb_solution` VALUES ('1260513759009574912', '1260513758992797696', '1.修改Samba密码，使密码符合公司密码复杂度要求，2.对未设置密码的用户添加密码，3.配置防火墙，只对业务ip开放Samba访问权限', '', '');

-- ----------------------------
-- Table structure for tb_statistics
-- ----------------------------
DROP TABLE IF EXISTS `tb_statistics`;
CREATE TABLE `tb_statistics`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `ipcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip数',
  `ipcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未下线ip数',
  `portcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口数',
  `portcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未下线端口数',
  `checkresultcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测结果数',
  `checkresultcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未修复检测结果数',
  `infocount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信息检测结果数',
  `lowcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '低危检测结果数',
  `mediumcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '中危检测结果数',
  `highcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高危检测结果数',
  `criticalcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '严重检测结果数',
  `fatalcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '致命检测结果数',
  `infocountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未修复信息检测结果数',
  `lowcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未修复低危检测结果数',
  `mediumcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未修复中危检测结果数',
  `highcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未修复高危检测结果数',
  `criticalcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未修复严重检测结果数',
  `fatalcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未修复致命检测结果数',
  `riskportcount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高危端口数',
  `riskportcountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未下线高危端口数',
  `riskservicecount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高危服务数',
  `riskservicecountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未下线高危服务数',
  `riskversioncount` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高危版本数',
  `riskversioncountonline` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未下线高危版本数',
  `updatetime` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '统计数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_statistics
-- ----------------------------

-- ----------------------------
-- Table structure for tb_task
-- ----------------------------
DROP TABLE IF EXISTS `tb_task`;
CREATE TABLE `tb_task`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务编号',
  `taskparentid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务父编号',
  `projectid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目编号',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `description` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务描述',
  `cronexpression` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'cron表达式',
  `crontask` tinyint(1) NULL DEFAULT NULL COMMENT 'cron任务',
  `starttime` datetime(0) NULL DEFAULT NULL COMMENT '任务开始时间',
  `endtime` datetime(0) NULL DEFAULT NULL COMMENT '任务结束时间',
  `worktype` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务类型',
  `checktype` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测类型',
  `threadnumber` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '线程数量',
  `singleipscantime` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '单个ip扫描次数',
  `additionoption` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务附加选项',
  `rate` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '扫描速率',
  `targetip` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '目标ip',
  `targetport` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '目标端口',
  `excludeip` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '排除ip',
  `ipslicesize` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '255' COMMENT '分组大小',
  `portslicesize` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口分组大小，nmap全端口模式时，如果该字段有值，则进行端口分组，分组大小范围：1000-10000',
  `dbipisexcludeip` tinyint(1) NULL DEFAULT NULL COMMENT 'db中ip作为排除ip',
  `merge2asset` tinyint(1) NULL DEFAULT NULL COMMENT '扫描结果合并到资产',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用于记录任务及配置信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_task
-- ----------------------------

-- ----------------------------
-- Table structure for tb_taskip
-- ----------------------------
DROP TABLE IF EXISTS `tb_taskip`;
CREATE TABLE `tb_taskip`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产ip编号',
  `taskid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务编号',
  `ipaddressv4` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ipv4地址',
  `ipaddressv6` varchar(39) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ipv6地址',
  `checkwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '安全检测白名单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '任务ip表，用于记录每次扫描任务的ip信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_taskip
-- ----------------------------

-- ----------------------------
-- Table structure for tb_taskpluginconfig
-- ----------------------------
DROP TABLE IF EXISTS `tb_taskpluginconfig`;
CREATE TABLE `tb_taskpluginconfig`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `taskid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务编号',
  `pluginconfigid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '任务插件，nse/selfd任务，或nse/selfd任务启用的插件' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_taskpluginconfig
-- ----------------------------

-- ----------------------------
-- Table structure for tb_taskport
-- ----------------------------
DROP TABLE IF EXISTS `tb_taskport`;
CREATE TABLE `tb_taskport`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '端口编号',
  `taskipid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务ip编号',
  `port` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '端口',
  `protocol` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口协议',
  `state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口开放状态',
  `service` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口服务',
  `version` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务版本',
  `checkwhitelist` tinyint(1) NULL DEFAULT NULL COMMENT '安全检测白名单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '任务端口表，用于记录每次扫描任务的端口信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_taskport
-- ----------------------------

-- ----------------------------
-- Table structure for tb_titlewhitelist
-- ----------------------------
DROP TABLE IF EXISTS `tb_titlewhitelist`;
CREATE TABLE `tb_titlewhitelist`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '参数编号',
  `title` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标题白名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_titlewhitelist
-- ----------------------------
INSERT INTO `tb_titlewhitelist` VALUES ('1164432912121204766', 'Welcome to nginx');
INSERT INTO `tb_titlewhitelist` VALUES ('1164432912121204767', 'Apache Tomcat');

-- ----------------------------
-- Table structure for tb_url
-- ----------------------------
DROP TABLE IF EXISTS `tb_url`;
CREATE TABLE `tb_url`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'url编号',
  `webinfoid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'web信息编号',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `url` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'url',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '当前页面所有url及url名称' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_url
-- ----------------------------

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户编号',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `admin` tinyint(1) NULL DEFAULT NULL COMMENT '是否管理员',
  `active` tinyint(1) NULL DEFAULT NULL COMMENT '是否有效',
  `avatar` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `lastdate` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('1259704243355521024', 'MagiCude', '$2a$10$7wmPIhsnZS3/I1xrQQOtvep9J/GVt2ofofkF4365cAxoFP8E5Zjd6', 1, 1, '/favicon.ico', '2020-10-09 20:32:27');
-- ----------------------------
-- Table structure for tb_useragent
-- ----------------------------
DROP TABLE IF EXISTS `tb_useragent`;
CREATE TABLE `tb_useragent`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `useragent` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'useragent',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'useragent' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_useragent
-- ----------------------------
INSERT INTO `tb_useragent` VALUES ('1168817980281327616', 'Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BLA-AL00 Build/HUAWEIBLA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36');
INSERT INTO `tb_useragent` VALUES ('1168817980558151680', 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50');
INSERT INTO `tb_useragent` VALUES ('1168817981279571968', 'Opera/9.80 (Windows NT 6.1; U; zh-cn) Presto/2.9.168 Version/11.50');
INSERT INTO `tb_useragent` VALUES ('1168817981397012480', 'Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0');
INSERT INTO `tb_useragent` VALUES ('1168817981434761216', 'Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10');

-- ----------------------------
-- Table structure for tb_vuln
-- ----------------------------
DROP TABLE IF EXISTS `tb_vuln`;
CREATE TABLE `tb_vuln`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '漏洞编号',
  `categorysecondid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞二级分类编号',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞名称',
  `description` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞描述',
  `risk` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞风险级别',
  `refer` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参考',
  `impactscope` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '影响范围',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '漏洞，用于记录漏洞信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_vuln
-- ----------------------------
INSERT INTO `tb_vuln` VALUES ('1252508972280320000', '1252508922875613184', '默认信息', '默认信息描述', '信息', '默认信息参考', '默认信息影响范围');
INSERT INTO `tb_vuln` VALUES ('1252508972448092160', '1252508922875613184', '默认低危', '默认低危描述', '低危', '默认低危参考', '默认低危影响范围');
INSERT INTO `tb_vuln` VALUES ('1252508972586504192', '1252508922875613184', '默认中危', '默认中危描述', '中危', '默认中危参考', '默认中危影响范围');
INSERT INTO `tb_vuln` VALUES ('1252508972666195968', '1252508922875613184', '默认高危', '默认高危描述', '高危', '默认高危参考', '默认高危影响范围');
INSERT INTO `tb_vuln` VALUES ('1252508972750082048', '1252508922875613184', '默认严重', '默认严重描述', '严重', '默认严重参考', '默认严重影响范围');
INSERT INTO `tb_vuln` VALUES ('1252508972833968128', '1252508922875613184', '默认致命', '默认致命描述', '致命', '默认致命参考', '默认致命影响范围');
INSERT INTO `tb_vuln` VALUES ('1260513758078439424', '1252508921818648576', 'Redis弱密码/未授权访问', 'Redis因配置不当未授权访问或密码较弱，可执行info命令。攻击者可以访问到内部数据，可导致敏感信息泄露，也可以执行flushall来清空所有数据。如果Redis以root身份运行，可以给root账户写入SSH公钥文件，直接通过SSH登录受害服务器', '高危', '', '所有redis版本');
INSERT INTO `tb_vuln` VALUES ('1260513758166519808', '1252508921818648576', 'Memcached未授权访问', 'Memcache未设置访问权限，可以执行stats命令，攻击者可以获取敏感数据', '中危', '', '所有Memcache版本');
INSERT INTO `tb_vuln` VALUES ('1260513758237822976', '1252508921818648576', 'FTP弱密码/未授权访问', 'ftp弱密码或因配置不当未授权访问，可能导致敏感信息泄露', '中危', '', '所有ftp版本');
INSERT INTO `tb_vuln` VALUES ('1260513758384623616', '1252508921487298560', 'SSH弱密码', 'ssh弱密码，可直接远程登录服务器，导致服务器被攻陷', '严重', '', '所有SSH版本');
INSERT INTO `tb_vuln` VALUES ('1260513758468509696', '1252508921818648576', 'MongoDB未授权访问', '对外开放的MongoDB服务，未配置访问认证授权，无需认证连接数据库后对数据库进行任意操作（增、删、改、查高危动作），存在严重的数据泄露风险', '中危', 'https://www.freebuf.com/vuls/212799.html', '所有MongoDB版本');
INSERT INTO `tb_vuln` VALUES ('1260513758560784384', '1252508921818648576', 'Zookeeper未授权访问', 'ZooKeeper未进行访问控制，攻击者可通过执行envi命令获得系统大量的敏感信息，包括系统名称、Java环境等', '中危', '', '所有Zookeeper版本');
INSERT INTO `tb_vuln` VALUES ('1260513758653059072', '1252508921818648576', 'MySQL弱密码/未授权访问', 'MySQL存在未设置密码的用户或存在弱密码的用户，攻击者可以获取数据库数据，即平常所说的拖库', '严重', '', '所有MySQL版本');
INSERT INTO `tb_vuln` VALUES ('1260513758720167936', '1252508922196135936', 'Nginx版本低于最新版/已不受支持', '当前Nginx版本不是最新的主线版本或稳定版，可能已不受支持，如版本较低，可能包含多个漏洞', '低危', 'http://nginx.org/en/download.html', '不在最新主线版本或稳定版本的Nginx');
INSERT INTO `tb_vuln` VALUES ('1260513758787276800', '1252508921818648576', 'MS SQL Server弱密码/未授权访问', 'MS SQL Server存在未设置密码的用户或存在弱密码的用户，攻击者可以获取数据库数据，即平常所说的拖库', '严重', '', '所有MS SQL Server版本');
INSERT INTO `tb_vuln` VALUES ('1260513758854385664', '1252508921818648576', 'Oracle弱密码/未授权访问', 'Oracle存在未设置密码的用户或存在弱密码的用户，攻击者可以获取数据库数据，即平常所说的拖库', '严重', '', '所有Oracle版本');
INSERT INTO `tb_vuln` VALUES ('1260513758921494528', '1252508921818648576', 'PostgreSQL弱密码/未授权访问', 'PostgreSQL存在未设置密码的用户或存在弱密码的用户，攻击者可以获取数据库数据，即平常所说的拖库', '严重', '', '所有PostgreSQL版本');
INSERT INTO `tb_vuln` VALUES ('1260513758992797696', '1252508921818648576', 'Samba弱密码/未授权访问', 'Samba未设置访问权限或存在弱密码的用户，攻击者可以获取共享的数据；如果用户能够远程登录，则服务器沦陷', '致命', '', '所有Samba版本');

-- ----------------------------
-- Table structure for tb_vulnkeyword
-- ----------------------------
DROP TABLE IF EXISTS `tb_vulnkeyword`;
CREATE TABLE `tb_vulnkeyword`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '漏洞关键字编号',
  `pluginconfigid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件配置编号',
  `keyword` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞关键字',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '漏洞关键字，用于记录漏洞关键字' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_vulnkeyword
-- ----------------------------
INSERT INTO `tb_vulnkeyword` VALUES ('1298634974383181824', '1298634973577875456', 'redis_version');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634974878109696', '1298634973577875456', 'Server');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634975117185024', '1298634973577875456', 'Clients');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634975293345792', '1298634973577875456', 'connected_clients');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634975586947072', '1298634973577875456', 'process_id');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634975821828096', '1298634975670833152', 'uptime');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634976190926848', '1298634976069292032', 'zookeeper');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634976388059136', '1298634976283201536', 'local');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634976820072448', '1298634976555831296', 'root');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634976962678784', '1298634976555831296', 'shutdown');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634977063342080', '1298634976555831296', 'nobody');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634977231114240', '1298634977138839552', 'mysql');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634977524715520', '1298634977419857920', 'FTPWeakPass');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634977650544640', '1298634977591824384', 'out');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634977839288320', '1298634977734430720', 'master');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634978179026944', '1298634977918980096', 'driver');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634978325827584', '1298634978246135808', 'postgres');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634978485211136', '1298634978397130752', '/');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634978636206080', '1298634978397130752', '$');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634978778812416', '1298634978690732032', 'Process ID');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634978900447232', '1298634978850115584', 'X server access is granted');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634979030470656', '1298634978967556096', 'VULNERABLE');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634979290517504', '1298634979105968128', 'VULNERABLE');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634979743502336', '1298634979529592832', 'VULNERABLE');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634980129378304', '1298634979865137152', 'VULNERABLE');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634980481699840', '1298634980204875776', 'account has empty password');
INSERT INTO `tb_vulnkeyword` VALUES ('1298634980817244160', '1298634980557197312', 'Process ID');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635549292236800', '1298635549027995648', 'redis_version');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635549409677312', '1298635549027995648', 'Server');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635549535506432', '1298635549027995648', 'Clients');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635549652946944', '1298635549027995648', 'connected_clients');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635549724250112', '1298635549027995648', 'process_id');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635549887827968', '1298635549791358976', 'uptime');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635550143680512', '1298635550038822912', 'zookeeper');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635550336618496', '1298635550223372288', 'local');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635550630219776', '1298635550437281792', 'root');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635550823157760', '1298635550437281792', 'shutdown');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635550978347008', '1298635550437281792', 'nobody');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635551225810944', '1298635551074816000', 'mysql');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635551645241344', '1298635551494246400', 'FTPWeakPass');
INSERT INTO `tb_vulnkeyword` VALUES ('1298635551796236288', '1298635551737516032', 'out');
INSERT INTO `tb_vulnkeyword` VALUES ('1305054772423954432', '1305054770993696768', '/_cat/count');

-- ----------------------------
-- Table structure for tb_vulnpluginconfig
-- ----------------------------
DROP TABLE IF EXISTS `tb_vulnpluginconfig`;
CREATE TABLE `tb_vulnpluginconfig`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `vulnid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '漏洞编号',
  `pluginconfigid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件配置编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '漏洞对应的插件配置表，用于检测漏洞的插件' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_vulnpluginconfig
-- ----------------------------
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634974685171712', '1260513758078439424', '1298634973577875456');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634975880548352', '1260513758166519808', '1298634975670833152');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634976237064192', '1260513758560784384', '1298634976069292032');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634976430002176', '1260513758468509696', '1298634976283201536');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634976887181312', '1260513758384623616', '1298634976555831296');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634977289834496', '1260513758653059072', '1298634977138839552');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634977554075648', '1260513758237822976', '1298634977419857920');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634977696681984', '1260513758720167936', '1298634977591824384');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634977877037056', '1260513758787276800', '1298634977734430720');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634978212581376', '1260513758854385664', '1298634977918980096');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634978355187712', '1260513758921494528', '1298634978246135808');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634978527154176', '1260513758992797696', '1298634978397130752');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634978812366848', '1260513758166519808', '1298634978690732032');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634978934001664', '1252508972666195968', '1298634978850115584');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634979068219392', '1252508972750082048', '1298634978967556096');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634979491844096', '1252508972666195968', '1298634979105968128');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634979827388416', '1252508972666195968', '1298634979529592832');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634980162932736', '1252508972666195968', '1298634979865137152');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634980519448576', '1260513758653059072', '1298634980204875776');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298634980959850496', '1260513758078439424', '1298634980557197312');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298635549329985536', '1260513758078439424', '1298635549027995648');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298635549925576704', '1260513758166519808', '1298635549791358976');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298635550181429248', '1260513758560784384', '1298635550038822912');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298635550374367232', '1260513758468509696', '1298635550223372288');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298635550709911552', '1260513758384623616', '1298635550437281792');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298635551301308416', '1260513758653059072', '1298635551074816000');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298635551695572992', '1260513758237822976', '1298635551494246400');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1298635551825596416', '1260513758720167936', '1298635551737516032');
INSERT INTO `tb_vulnpluginconfig` VALUES ('1305054772977602560', '1252508972666195968', '1305054770993696768');

-- ----------------------------
-- Table structure for tb_webinfo
-- ----------------------------
DROP TABLE IF EXISTS `tb_webinfo`;
CREATE TABLE `tb_webinfo`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'web信息编号',
  `portid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口编号',
  `titlewhitelistid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题白名单编号',
  `title` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面标题',
  `bodychildrenstextcontent` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'body子节点文本内容',
  `server` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '响应头中的服务',
  `xpoweredby` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'xpoweredby',
  `setcookie` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '设置cookie',
  `wwwauthenticate` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '认证方式',
  `appname` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名称',
  `appversion` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用版本',
  `devlanguage` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开发语言',
  `crawltime` datetime(0) NULL DEFAULT NULL COMMENT '页面抓取时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '网站信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_webinfo
-- ----------------------------

-- ----------------------------
-- Table structure for tb_webrawdata
-- ----------------------------
DROP TABLE IF EXISTS `tb_webrawdata`;
CREATE TABLE `tb_webrawdata`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `webinfoid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'webinfo编号',
  `header` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '响应头',
  `response` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '响应',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'web原始数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_webrawdata
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
