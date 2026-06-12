USE tiyu;

UPDATE t_user
SET role = 'ADMIN'
WHERE username = 'admin';

UPDATE t_user
SET role = 'STUDENT',
    password = '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a'
WHERE username = 'zhangsan';

UPDATE t_user
SET role = 'TEACHER',
    password = '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a'
WHERE username = 'lisi';

UPDATE t_user
SET role = 'STAFF',
    password = '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a'
WHERE username = 'wangwu';

INSERT INTO t_user(username, password, email, role, created_at)
SELECT 'lisi', '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a', 'lisi@example.com', 'TEACHER', NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'lisi');

INSERT INTO t_user(username, password, email, role, created_at)
SELECT 'wangwu', '$2a$10$K/FVXTeoHZfJYsBDGEWGSuybHyp4oSTvHZiLcokmIIwRdsVsq4.2a', 'wangwu@example.com', 'STAFF', NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'wangwu');
