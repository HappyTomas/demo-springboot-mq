--
-- Current Database: `demo_mq`
--
CREATE DATABASE demo_mq CHARACTER SET utf8 COLLATE utf8_bin;
USE demo_mq;


--
-- Table structure for table `mq_sender`
--
DROP TABLE IF EXISTS `mq_sender`;
CREATE TABLE `mq_sender` (
`topic` VARCHAR(100) NOT NULL COMMENT '消息主题' ,
`msg_text` VARCHAR(4000) NOT NULL COMMENT '消息内容' ,
`msg_time` datetime NOT NULL COMMENT '消息发送时间' ,
`msg_id` VARCHAR(100) NOT NULL COMMENT '消息唯一ID' ,
`status` VARCHAR(2) NOT NULL COMMENT '消息状态：0失败，1成功' ,
`system_name` VARCHAR(100) NULL DEFAULT NULL COMMENT '系统名称' ,
`last_resend_time` datetime NULL DEFAULT NULL COMMENT '重新发送成功的时间' ,
PRIMARY KEY (`msg_id`),
INDEX `mq_sender_index2` (`system_name`, `topic`) USING BTREE ,
INDEX `mq_sender_index1` (`topic`, `status`) USING BTREE ,
INDEX `mq_sender_index3` (`msg_time`, `status`, `topic`) USING BTREE
) ENGINE=InnoDB COMMENT='mq发送记录表';

--
-- Table structure for table `mq_receiver`
--
DROP TABLE IF EXISTS `mq_receiver`;
CREATE TABLE `mq_receiver` (
`topic_queue` VARCHAR(100) NOT NULL COMMENT '消息队列' ,
`msg_text` VARCHAR(4000) NOT NULL COMMENT '消息内容' ,
`msg_time` datetime NOT NULL COMMENT '消息时间' ,
`msg_id` VARCHAR(100) NOT NULL COMMENT '消息唯一ID' ,
`status` VARCHAR(2) NOT NULL COMMENT '消息状态' ,
`system_name` VARCHAR(100) NULL DEFAULT NULL COMMENT '系统名称' ,
UNIQUE INDEX `mq_receiver_index3` (`topic_queue`, `msg_id`) USING BTREE ,
UNIQUE INDEX `mq_receiver_index1` (`system_name`, `topic_queue`, `msg_id`) USING BTREE ,
INDEX `mq_receiver_index2` (`msg_time`, `topic_queue`) USING BTREE
) ENGINE=InnoDB COMMENT='mq接收记录表';

--
-- Table structure for table `mq_compare`
--
DROP TABLE IF EXISTS `mq_compare`;
CREATE TABLE `mq_compare` (
`id` bigint(14) NOT NULL AUTO_INCREMENT ,
`sender_topic` VARCHAR(100) NULL DEFAULT NULL COMMENT '发送方主题' ,
`receiver_queue` VARCHAR(200) NULL DEFAULT NULL COMMENT '消费方队列' ,
`sender_count` bigint(14) NULL DEFAULT NULL COMMENT '发送总数量' ,
`receiver_count` bigint(14) NULL DEFAULT NULL COMMENT '消费总数量' ,
`compare_date` VARCHAR(100) NULL DEFAULT NULL COMMENT '比对的数据日期' ,
`create_time` datetime NULL DEFAULT NULL COMMENT '创建日期' ,
PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='mq比对表';


--
-- Dumping data for table `all`
--
