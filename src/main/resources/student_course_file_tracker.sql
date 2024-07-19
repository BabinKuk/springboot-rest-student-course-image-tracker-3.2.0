CREATE DATABASE  IF NOT EXISTS `student-course-file-tracker`;
USE `student-course-file-tracker`;
--
-- Table structure for tables `user`,`instructor`,`instructor_detail`,`student`, `course`, `review`, `course_student` and `image`
--

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `instructor_detail`;

CREATE TABLE `instructor_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `youtube_channel` varchar(128) DEFAULT NULL,
  `hobby` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `student`;

CREATE TABLE `student` (
  `id` int DEFAULT NULL,
  `street` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `zip_code` varchar(45) DEFAULT NULL,
  KEY `FK_USER_STUDENT_idx` (`id`),
  CONSTRAINT `FK_USER_STUDENT` FOREIGN KEY (`id`) 
  REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `instructor`;

CREATE TABLE `instructor` (
  `id` int DEFAULT NULL,
  `salary` double DEFAULT NULL,
  `instructor_detail_id` int(11) DEFAULT NULL,
  KEY `FK_USER_idx` (`id`),
  CONSTRAINT `FK_USER_INSTRUCTOR` FOREIGN KEY (`id`) 
  REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  KEY `FK_USER_INSTRUCTOR_idx` (`instructor_detail_id`),
  CONSTRAINT `FK_DETAIL` FOREIGN KEY (`instructor_detail_id`) 
  REFERENCES `instructor_detail` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `image`;

CREATE TABLE `image` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `file_name` varchar(45) DEFAULT NULL,
  `data` MEDIUMBLOB  DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_USER_IMAGE_idx` (`user_id`),
  CONSTRAINT `FK_USER_IMAGE` FOREIGN KEY (`user_id`) 
  REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) DEFAULT NULL,
  `instructor_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `TITLE_UNIQUE` (`title`),
  KEY `FK_INSTRUCTOR_idx` (`instructor_id`),
  CONSTRAINT `FK_INSTRUCTOR`  FOREIGN KEY (`instructor_id`) 
  REFERENCES `instructor` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `review`;

CREATE TABLE `review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment` varchar(256) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_COURSE_ID_idx` (`course_id`),
  CONSTRAINT `FK_COURSE` FOREIGN KEY (`course_id`) 
  REFERENCES `course` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `course_student`;

CREATE TABLE `course_student` (
  `course_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  PRIMARY KEY (`course_id`,`student_id`),
  KEY `FK_STUDENT_idx` (`student_id`),
  CONSTRAINT `FK_COURSE_05` FOREIGN KEY (`course_id`) 
  REFERENCES `course` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_STUDENT` FOREIGN KEY (`student_id`) 
  REFERENCES `student` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `change_log_item`;

DROP TABLE IF EXISTS `change_log`;

DROP TABLE IF EXISTS `log_module`;

CREATE TABLE `log_module` (
  `lm_id` int(11) NOT NULL AUTO_INCREMENT,
  `lm_description` varchar(256) DEFAULT NULL,
  `lm_entity_name` varchar(256),
  PRIMARY KEY (`lm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `change_log` (
  `chlo_id` int(11) NOT NULL AUTO_INCREMENT,
  `chlo_timestamp` timestamp(6) DEFAULT NULL,
  `chlo_user_id` varchar(256) DEFAULT NULL,
  `chlo_table_id` int(11) DEFAULT NULL,
  `chlo_lm_id` int(11) DEFAULT NULL,
  KEY `FK_CHLO_LM_idx` (`chlo_lm_id`),
  CONSTRAINT `FK_CHLO_LM` FOREIGN KEY (`chlo_lm_id`) 
  REFERENCES `log_module` (`lm_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  PRIMARY KEY (`chlo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `change_log_item` (
  `chli_id` int(11) NOT NULL AUTO_INCREMENT,
  `chli_field_name` varchar(256) DEFAULT NULL,
  `chli_old_value` varchar(256) DEFAULT NULL,
  `chli_old_value_id` int(11) NOT NULL,
  `chli_new_value` varchar(256) DEFAULT NULL,
  `chli_new_value_id` int(11) NOT NULL,
  `chlo_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`chli_id`),
  KEY `FK_CHANGE_LOG_idx` (`chlo_id`),
  CONSTRAINT `FK_CHANGE_LOG`  FOREIGN KEY (`chlo_id`) 
  REFERENCES `change_log` (`chlo_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

SET FOREIGN_KEY_CHECKS = 1;
