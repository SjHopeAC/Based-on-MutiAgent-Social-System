-- 通知测试数据插入语句

-- 1. 关注通知
INSERT INTO user_notice (user_id, from_user_id, notice_type, target_type, target_id, content, is_read, create_time, from_user_nickname, from_user_avatar)
VALUES 
(1, 2, 1, 0, 0, '用户李四关注了您', 0, NOW(), '李四', 'http://localhost:8088/api/upload/avatar/2_avatar.jpg'),
(1, 3, 1, 0, 0, '用户王五关注了您', 0, NOW() - INTERVAL 1 HOUR, '王五', 'http://localhost:8088/api/upload/avatar/3_avatar.jpg');

-- 2. 点赞通知
INSERT INTO user_notice (user_id, from_user_id, notice_type, target_type, target_id, content, is_read, create_time, from_user_nickname, from_user_avatar)
VALUES 
(1, 4, 2, 1, 1, '用户赵六点赞了您的帖子《Spring Boot + MyBatis 注解开发真的太香了》', 0, NOW() - INTERVAL 2 HOUR, '赵六', 'http://localhost:8088/api/upload/avatar/4_avatar.jpg'),
(1, 5, 2, 1, 1, '用户钱七点赞了您的帖子《Spring Boot + MyBatis 注解开发真的太香了》', 1, NOW() - INTERVAL 3 HOUR, '钱七', 'http://localhost:8088/api/upload/avatar/5_avatar.jpg');

-- 3. 评论通知
INSERT INTO user_notice (user_id, from_user_id, notice_type, target_type, target_id, content, is_read, create_time, from_user_nickname, from_user_avatar)
VALUES 
(1, 6, 3, 1, 1, '用户孙八评论了您的帖子：这篇文章很有帮助！', 0, NOW() - INTERVAL 4 HOUR, '孙八', 'http://localhost:8088/api/upload/avatar/6_avatar.jpg'),
(1, 7, 3, 1, 2, '用户周九评论了您的帖子：学习了，感谢分享！', 0, NOW() - INTERVAL 5 HOUR, '周九', 'http://localhost:8088/api/upload/avatar/7_avatar.jpg');

-- 4. 系统通知
INSERT INTO user_notice (user_id, from_user_id, notice_type, target_type, target_id, content, is_read, create_time, from_user_nickname, from_user_avatar)
VALUES 
(1, 0, 5, 0, 0, '您的帖子《Spring Boot + MyBatis 注解开发真的太香了》已被设为精华帖', 0, NOW() - INTERVAL 6 HOUR, '系统', 'http://localhost:8088/api/upload/avatar/system_avatar.png'),
(1, 0, 5, 0, 0, '欢迎加入阔论社区，祝您在这里度过愉快的时光！', 1, NOW() - INTERVAL 1 DAY, '系统', 'http://localhost:8088/api/upload/avatar/system_avatar.png');

-- 5. 私信通知
INSERT INTO user_notice (user_id, from_user_id, notice_type, target_type, target_id, content, is_read, create_time, from_user_nickname, from_user_avatar)
VALUES 
(1, 8, 4, 3, 1, '您好，我对您的帖子很感兴趣，想请教一些问题', 0, NOW() - INTERVAL 7 HOUR, '吴十', 'http://localhost:8088/api/upload/avatar/8_avatar.jpg');

-- 为其他用户生成一些通知
INSERT INTO user_notice (user_id, from_user_id, notice_type, target_type, target_id, content, is_read, create_time, from_user_nickname, from_user_avatar)
VALUES 
(2, 1, 1, 0, 0, '用户张三关注了您', 0, NOW(), '张三', 'http://localhost:8088/api/upload/avatar/1_avatar.jpg'),
(2, 3, 2, 1, 2, '用户王五点赞了您的帖子《B站多级评论的数据库设计思路分享》', 0, NOW() - INTERVAL 1 HOUR, '王五', 'http://localhost:8088/api/upload/avatar/3_avatar.jpg');
