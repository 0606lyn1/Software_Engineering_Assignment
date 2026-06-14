USE tiyu;

DELIMITER //
CREATE PROCEDURE add_column_if_missing(IN table_name_value VARCHAR(64), IN column_name_value VARCHAR(64), IN ddl_value TEXT)
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = table_name_value
      AND COLUMN_NAME = column_name_value
  ) THEN
    SET @sql_text = ddl_value;
    PREPARE stmt FROM @sql_text;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END//
DELIMITER ;

CALL add_column_if_missing('t_reservation', 'checkin_code', 'ALTER TABLE t_reservation ADD COLUMN checkin_code VARCHAR(32) NULL UNIQUE AFTER status');
CALL add_column_if_missing('t_reservation', 'checked_in_at', 'ALTER TABLE t_reservation ADD COLUMN checked_in_at DATETIME NULL AFTER checkin_code');
CALL add_column_if_missing('t_reservation', 'cancel_deadline', 'ALTER TABLE t_reservation ADD COLUMN cancel_deadline DATETIME NULL AFTER checked_in_at');
CALL add_column_if_missing('t_reservation', 'cancel_reason', 'ALTER TABLE t_reservation ADD COLUMN cancel_reason VARCHAR(512) NULL AFTER cancel_deadline');
CALL add_column_if_missing('t_reservation', 'appeal_reason', 'ALTER TABLE t_reservation ADD COLUMN appeal_reason VARCHAR(512) NULL AFTER cancel_reason');
CALL add_column_if_missing('t_reservation', 'appeal_status', 'ALTER TABLE t_reservation ADD COLUMN appeal_status VARCHAR(32) NOT NULL DEFAULT ''NONE'' AFTER appeal_reason');
CALL add_column_if_missing('t_reservation', 'created_at', 'ALTER TABLE t_reservation ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER appeal_status');

DROP PROCEDURE add_column_if_missing;

CREATE TABLE IF NOT EXISTS t_reservation_rule (
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

CREATE TABLE IF NOT EXISTS t_announcement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  level VARCHAR(32) NOT NULL DEFAULT 'INFO',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_user_notification (
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

INSERT INTO t_reservation_rule(venue_id, advance_days, cancel_before_hours, max_hours_per_booking, daily_limit, weekly_limit, open_time, close_time, slot_minutes)
SELECT NULL, 3, 4, 2, 2, 6, '06:00', '22:00', 60
WHERE NOT EXISTS (SELECT 1 FROM t_reservation_rule WHERE venue_id IS NULL);

INSERT INTO t_announcement(title, content, level, created_at)
SELECT '本周场馆开放安排', '羽毛球馆与篮球馆按 06:00-22:00 开放，泳池以维护状态为准。', 'INFO', NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_announcement WHERE title = '本周场馆开放安排');

INSERT INTO t_announcement(title, content, level, created_at)
SELECT '入场核销提醒', '预约成功后请在我的预约中查看核销码，到场后由场地负责人核销。', 'WARNING', NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_announcement WHERE title = '入场核销提醒');

UPDATE t_reservation
SET checkin_code = LPAD(id, 6, '0'),
    cancel_deadline = DATE_SUB(start_time, INTERVAL 4 HOUR),
    appeal_status = 'NONE'
WHERE checkin_code IS NULL;
