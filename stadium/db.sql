DROP DATABASE IF EXISTS tiyu;
CREATE DATABASE tiyu CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE tiyu;

CREATE TABLE t_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(128) NOT NULL UNIQUE,
  role VARCHAR(32) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_role(role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_venue_type (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_venue (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  type_id BIGINT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  description VARCHAR(512) NULL,
  notes VARCHAR(512) NULL,
  INDEX idx_venue_type(type_id),
  CONSTRAINT fk_venue_type FOREIGN KEY (type_id) REFERENCES t_venue_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_venue_ops (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  venue_id BIGINT NOT NULL UNIQUE,
  maintenance_status VARCHAR(32) NOT NULL DEFAULT 'NORMAL',
  cleaning_status VARCHAR(32) NOT NULL DEFAULT 'CLEAN',
  lighting_status VARCHAR(32) NOT NULL DEFAULT 'NORMAL',
  equipment_status VARCHAR(32) NOT NULL DEFAULT 'COMPLETE',
  responsible_person VARCHAR(64) NULL,
  contact_phone VARCHAR(32) NULL,
  last_inspector VARCHAR(64) NULL,
  last_checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remark VARCHAR(512) NULL,
  INDEX idx_venue_ops_status(maintenance_status, cleaning_status, lighting_status, equipment_status),
  CONSTRAINT fk_venue_ops_venue FOREIGN KEY (venue_id) REFERENCES t_venue(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  venue_id BIGINT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status VARCHAR(32) NOT NULL,
  checkin_code VARCHAR(32) NULL UNIQUE,
  checked_in_at DATETIME NULL,
  cancel_deadline DATETIME NULL,
  cancel_reason VARCHAR(512) NULL,
  appeal_reason VARCHAR(512) NULL,
  appeal_status VARCHAR(32) NOT NULL DEFAULT 'NONE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_reservation_user(user_id),
  INDEX idx_reservation_venue(venue_id),
  INDEX idx_reservation_time(start_time, end_time),
  CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES t_user(id),
  CONSTRAINT fk_reservation_venue FOREIGN KEY (venue_id) REFERENCES t_venue(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_reservation_rule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  venue_id BIGINT NULL UNIQUE,
  advance_days INT NOT NULL DEFAULT 3,
  cancel_before_hours INT NOT NULL DEFAULT 4,
  max_hours_per_booking INT NOT NULL DEFAULT 2,
  daily_limit INT NOT NULL DEFAULT 2,
  weekly_limit INT NOT NULL DEFAULT 6,
  open_time VARCHAR(8) NOT NULL DEFAULT '06:00',
  close_time VARCHAR(8) NOT NULL DEFAULT '22:00',
  slot_minutes INT NOT NULL DEFAULT 60,
  CONSTRAINT fk_rule_venue FOREIGN KEY (venue_id) REFERENCES t_venue(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_announcement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  level VARCHAR(32) NOT NULL DEFAULT 'INFO',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_user_notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  type VARCHAR(32) NOT NULL,
  read_flag TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_notification_user(user_id, read_flag, created_at),
  CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  venue_id BIGINT NOT NULL,
  content VARCHAR(1000) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_comment_venue(venue_id),
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES t_user(id),
  CONSTRAINT fk_comment_venue FOREIGN KEY (venue_id) REFERENCES t_venue(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 默认管理员：admin / admin123
INSERT INTO t_user(username, password, email, role, created_at) VALUES
('admin', '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a', 'admin@example.com', 'ADMIN', NOW()),
('zhangsan', '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a', 'zhangsan@example.com', 'STUDENT', NOW()),
('lisi', '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a', 'lisi@example.com', 'TEACHER', NOW()),
('wangwu', '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a', 'wangwu@example.com', 'STAFF', NOW());

INSERT INTO t_venue_type(name) VALUES
('羽毛球'), ('篮球'), ('游泳');

INSERT INTO t_venue(name, type_id, price, description, notes) VALUES
('羽毛球1号馆', 1, 80.00, '标准羽毛球场地，木地板', '请自带球拍'),
('篮球A馆', 2, 120.00, '全场篮球场地，含计时器', '仅支持整点预约'),
('恒温泳池', 3, 150.00, '25米四泳道恒温泳池', '需佩戴泳帽');

INSERT INTO t_venue_ops(
  venue_id, maintenance_status, cleaning_status, lighting_status, equipment_status,
  responsible_person, contact_phone, last_inspector, last_checked_at, remark
) VALUES
(1, 'NORMAL', 'CLEAN', 'NORMAL', 'COMPLETE', '李老师', '13800000001', 'admin', NOW(), '木地板已完成晨检，适合开放'),
(2, 'NORMAL', 'PENDING_RECHECK', 'NORMAL', 'COMPLETE', '王老师', '13800000002', 'admin', NOW(), '赛后清洁待复检，建议复核后开放'),
(3, 'MAINTENANCE', 'CLEAN', 'FAULT', 'COMPLETE', '赵老师', '13800000003', 'admin', NOW(), '恒温设备检修中，暂不开放预约');

INSERT INTO t_reservation(user_id, venue_id, start_time, end_time, status, checkin_code, cancel_deadline, appeal_status, created_at) VALUES
(2, 1, '2026-03-15 10:00:00', '2026-03-15 11:00:00', 'NO_SHOW', '123456', '2026-03-15 06:00:00', 'NONE', NOW());

INSERT INTO t_reservation_rule(venue_id, advance_days, cancel_before_hours, max_hours_per_booking, daily_limit, weekly_limit, open_time, close_time, slot_minutes) VALUES
(NULL, 3, 4, 2, 2, 6, '06:00', '22:00', 60),
(3, 2, 6, 1, 1, 3, '09:00', '20:00', 60);

INSERT INTO t_announcement(title, content, level, created_at) VALUES
('本周场馆开放安排', '羽毛球馆与篮球馆按 06:00-22:00 开放，泳池以维护状态为准。', 'INFO', NOW()),
('入场核销提醒', '预约成功后请在我的预约中查看核销码，到场后由场地负责人核销。', 'WARNING', NOW());

INSERT INTO t_user_notification(user_id, title, content, type, read_flag, created_at) VALUES
(2, '爽约记录提醒', '历史演示预约未核销，已记为爽约；可在我的预约中提交异常申诉。', 'VIOLATION', 0, NOW());

INSERT INTO t_comment(user_id, venue_id, content, created_at) VALUES
(2, 1, '场地很干净，灯光不错。', NOW());
