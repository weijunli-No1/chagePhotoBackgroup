-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `openid` varchar(64) NOT NULL COMMENT '微信 openid，唯一索引',
  `nickname` varchar(128) DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(256) DEFAULT NULL COMMENT '用户头像 URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 照片历史表
CREATE TABLE IF NOT EXISTS `t_photo_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `user_id` bigint(20) NOT NULL COMMENT '关联用户表 id',
  `photo_url` varchar(512) NOT NULL COMMENT '处理后证件照存储文件 URL',
  `original_photo_url` varchar(512) DEFAULT NULL COMMENT '原图 URL',
  `bg_color` varchar(16) DEFAULT NULL COMMENT '背景色值（如 #638cce）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '处理时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='照片历史表';

-- 用户每日使用统计表
CREATE TABLE IF NOT EXISTS `t_user_daily_usage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `record_date` date NOT NULL,
  `save_count` int(11) NOT NULL DEFAULT '0',
  `ad_view_count` int(11) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
