# SQL-Front 5.1  (Build 4.16)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;


# Host: localhost    Database: basesys
# ------------------------------------------------------
# Server version 5.5.38

#
# Source for table sys_organization
#

DROP TABLE IF EXISTS `sys_organization`;
CREATE TABLE `sys_organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `parent_ids` varchar(100) DEFAULT NULL,
  `deleteAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_organization_parent_id` (`parent_id`),
  KEY `idx_sys_organization_parent_ids` (`parent_ids`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;

#
# Dumping data for table sys_organization
#

LOCK TABLES `sys_organization` WRITE;
/*!40000 ALTER TABLE `sys_organization` DISABLE KEYS */;
INSERT INTO `sys_organization` VALUES (1,'总公司',0,'0,',NULL);
INSERT INTO `sys_organization` VALUES (40,'信息院',1,'0,1,',NULL);
INSERT INTO `sys_organization` VALUES (41,'移动院',1,'0,1,',NULL);
INSERT INTO `sys_organization` VALUES (42,'金融数据产品部d',40,'0,1,40,',NULL);
/*!40000 ALTER TABLE `sys_organization` ENABLE KEYS */;
UNLOCK TABLES;

#
# Source for table sys_resource
#

DROP TABLE IF EXISTS `sys_resource`;
CREATE TABLE `sys_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `url` varchar(200) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `parent_ids` varchar(100) DEFAULT NULL,
  `permission` varchar(100) DEFAULT NULL,
  `deleteAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_resource_parent_id` (`parent_id`),
  KEY `idx_sys_resource_parent_ids` (`parent_ids`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

#
# Dumping data for table sys_resource
#

LOCK TABLES `sys_resource` WRITE;
/*!40000 ALTER TABLE `sys_resource` DISABLE KEYS */;
INSERT INTO `sys_resource` VALUES (1,'资源','menu','',0,'0,','*:*',NULL);
INSERT INTO `sys_resource` VALUES (11,'组织机构管理','menu','/organization_tree',1,'0,1,','organization:*',NULL);
INSERT INTO `sys_resource` VALUES (12,'组织机构新增','button','',11,'0,1,11,','organization:create',NULL);
INSERT INTO `sys_resource` VALUES (13,'组织机构修改','button','',11,'0,1,11,','organization:update',NULL);
INSERT INTO `sys_resource` VALUES (14,'组织机构删除','button','',11,'0,1,11,','organization:delete',NULL);
INSERT INTO `sys_resource` VALUES (15,'组织机构查看','button','',11,'0,1,11,','organization:view',NULL);
INSERT INTO `sys_resource` VALUES (21,'用户管理','menu','/user_list',1,'0,1,','user:*',NULL);
INSERT INTO `sys_resource` VALUES (22,'用户新增','button','',21,'0,1,21,','user:create',NULL);
INSERT INTO `sys_resource` VALUES (23,'用户修改','button','',21,'0,1,21,','user:update',NULL);
INSERT INTO `sys_resource` VALUES (24,'用户删除','button','',21,'0,1,21,','user:delete',NULL);
INSERT INTO `sys_resource` VALUES (25,'用户查看','button','',21,'0,1,21,','user:view',NULL);
INSERT INTO `sys_resource` VALUES (31,'资源管理','menu','/resource_list',1,'0,1,','resource:*',NULL);
INSERT INTO `sys_resource` VALUES (32,'资源新增','button','',31,'0,1,31,','resource:create',NULL);
INSERT INTO `sys_resource` VALUES (33,'资源修改','button','',31,'0,1,31,','resource:update',NULL);
INSERT INTO `sys_resource` VALUES (34,'资源删除','button','',31,'0,1,31,','resource:delete',NULL);
INSERT INTO `sys_resource` VALUES (35,'资源查看','button','',31,'0,1,31,','resource:view',NULL);
INSERT INTO `sys_resource` VALUES (41,'角色管理','menu','/role_list',1,'0,1,','role:*',NULL);
INSERT INTO `sys_resource` VALUES (42,'角色新增','button','',41,'0,1,41,','role:create',NULL);
INSERT INTO `sys_resource` VALUES (43,'角色修改','button','',41,'0,1,41,','role:update',NULL);
INSERT INTO `sys_resource` VALUES (44,'角色删除','button','',41,'0,1,41,','role:delete',NULL);
INSERT INTO `sys_resource` VALUES (45,'角色查看','button','',41,'0,1,41,','role:view',NULL);
INSERT INTO `sys_resource` VALUES (46,'日志管理','menu','/log_list',1,'0,1,','log:*',NULL);
INSERT INTO `sys_resource` VALUES (47,'日志查看','button','',46,'0,1,46,','log:view',NULL);
/*!40000 ALTER TABLE `sys_resource` ENABLE KEYS */;
UNLOCK TABLES;

#
# Source for table sys_role
#

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `resource_ids` varchar(100) DEFAULT NULL,
  `deleteAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_role_resource_ids` (`resource_ids`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

#
# Dumping data for table sys_role
#

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'admin','超级管理员','1,46,47,41,45,44,43,42,31,35,34,33,32,21,25,24,23,22,11,15,14,13,12',NULL);
INSERT INTO `sys_role` VALUES (8,'dfd','dfds','45',NULL);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

#
# Source for table sys_user
#

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `organization_id` bigint(20) DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `salt` varchar(100) DEFAULT NULL,
  `role_ids` varchar(100) DEFAULT NULL,
  `locked` tinyint(1) DEFAULT '0',
  `deleteAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_sys_user_username` (`username`),
  KEY `idx_sys_user_organization_id` (`organization_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

#
# Dumping data for table sys_user
#

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,1,'admin','d3c59d25033dbf980d29554025c23a75','8d78869f470951332959580424d4bf4f','1',0,NULL);
INSERT INTO `sys_user` VALUES (17,41,'admin1','17f1d3bae273972f96a1b3a265d987de','6cf1fccfd9c3718f2b2350a60cc50aa0','8',NULL,NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

#
# Source for table sys_user_log
#

DROP TABLE IF EXISTS `sys_user_log`;
CREATE TABLE `sys_user_log` (
  `user_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `module_name` varchar(225) DEFAULT NULL,
  `operate` varchar(255) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `class_name` varchar(225) DEFAULT NULL,
  `method_name` varchar(225) DEFAULT NULL,
  `params` varchar(255) DEFAULT NULL,
  `ip` varchar(225) DEFAULT NULL,
  `deleteAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

#
# Dumping data for table sys_user_log
#

LOCK TABLES `sys_user_log` WRITE;
/*!40000 ALTER TABLE `sys_user_log` DISABLE KEYS */;
INSERT INTO `sys_user_log` VALUES (1,1,NULL,'create','2018-07-20 17:50:48','cn.shendu.controller.UserController','create','[{\"credentialsSalt\":\"admin16cf1fccfd9c3718f2b2350a60cc50aa0\",\"id\":17,\"organization_id\":41,\"password\":\"17f1d3bae273972f96a1b3a265d987de\",\"role_ids\":\"1\",\"salt\":\"6cf1fccfd9c3718f2b2350a60cc50aa0\",\"username\":\"admin1\"},[\"1\"]]','127.0.0.1',NULL);
/*!40000 ALTER TABLE `sys_user_log` ENABLE KEYS */;
UNLOCK TABLES;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
