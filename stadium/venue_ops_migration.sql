USE tiyu;

CREATE TABLE IF NOT EXISTS t_venue_ops (
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

INSERT INTO t_venue_ops(
  venue_id, maintenance_status, cleaning_status, lighting_status, equipment_status,
  responsible_person, contact_phone, last_inspector, last_checked_at, remark
) VALUES
(1, 'NORMAL', 'CLEAN', 'NORMAL', 'COMPLETE', '李老师', '13800000001', 'admin', NOW(), '木地板已完成晨检，适合开放'),
(2, 'NORMAL', 'PENDING_RECHECK', 'NORMAL', 'COMPLETE', '王老师', '13800000002', 'admin', NOW(), '赛后清洁待复检，建议复核后开放'),
(3, 'MAINTENANCE', 'CLEAN', 'FAULT', 'COMPLETE', '赵老师', '13800000003', 'admin', NOW(), '恒温设备检修中，暂不开放预约')
ON DUPLICATE KEY UPDATE
  maintenance_status = VALUES(maintenance_status),
  cleaning_status = VALUES(cleaning_status),
  lighting_status = VALUES(lighting_status),
  equipment_status = VALUES(equipment_status),
  responsible_person = VALUES(responsible_person),
  contact_phone = VALUES(contact_phone),
  last_inspector = VALUES(last_inspector),
  last_checked_at = VALUES(last_checked_at),
  remark = VALUES(remark);
