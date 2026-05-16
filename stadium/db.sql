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

CREATE TABLE t_reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  venue_id BIGINT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status VARCHAR(32) NOT NULL,
  INDEX idx_reservation_user(user_id),
  INDEX idx_reservation_venue(venue_id),
  INDEX idx_reservation_time(start_time, end_time),
  CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES t_user(id),
  CONSTRAINT fk_reservation_venue FOREIGN KEY (venue_id) REFERENCES t_venue(id)
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
('admin', '$2a$10$8QH1Olk9hK7o4VQ6O0skB.5M9rG4w9P34J68UYoXhYwIMfHAdXQdS', 'admin@example.com', 'ADMIN', NOW()),
('zhangsan', '$2a$10$8QH1Olk9hK7o4VQ6O0skB.5M9rG4w9P34J68UYoXhYwIMfHAdXQdS', 'zhangsan@example.com', 'USER', NOW());

INSERT INTO t_venue_type(name) VALUES
('羽毛球'), ('篮球'), ('游泳');

INSERT INTO t_venue(name, type_id, price, description, notes) VALUES
('羽毛球1号馆', 1, 80.00, '标准羽毛球场地，木地板', '请自带球拍'),
('篮球A馆', 2, 120.00, '全场篮球场地，含计时器', '仅支持整点预约'),
('恒温泳池', 3, 150.00, '25米四泳道恒温泳池', '需佩戴泳帽');

INSERT INTO t_reservation(user_id, venue_id, start_time, end_time, status) VALUES
(2, 1, '2026-03-15 10:00:00', '2026-03-15 11:00:00', 'BOOKED');

INSERT INTO t_comment(user_id, venue_id, content, created_at) VALUES
(2, 1, '场地很干净，灯光不错。', NOW());
