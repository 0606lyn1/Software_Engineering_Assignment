SET @email_reminder_column_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 't_user'
    AND COLUMN_NAME = 'email_reminder_enabled'
);
SET @email_reminder_column_sql := IF(
  @email_reminder_column_exists = 0,
  'ALTER TABLE t_user ADD COLUMN email_reminder_enabled TINYINT(1) NOT NULL DEFAULT 1 AFTER role',
  'SELECT 1'
);
PREPARE email_reminder_column_stmt FROM @email_reminder_column_sql;
EXECUTE email_reminder_column_stmt;
DEALLOCATE PREPARE email_reminder_column_stmt;

CREATE TABLE IF NOT EXISTS t_reminder_delivery (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  channel VARCHAR(32) NOT NULL,
  reminder_type VARCHAR(32) NOT NULL,
  status VARCHAR(32) NOT NULL,
  scheduled_at DATETIME NOT NULL,
  sent_at DATETIME NULL,
  attempt_count INT NOT NULL DEFAULT 0,
  error_message VARCHAR(512) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_reminder_once(reservation_id, channel, reminder_type),
  INDEX idx_reminder_scan(status, scheduled_at),
  INDEX idx_reminder_user(user_id, created_at),
  CONSTRAINT fk_reminder_reservation FOREIGN KEY (reservation_id) REFERENCES t_reservation(id),
  CONSTRAINT fk_reminder_user FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
