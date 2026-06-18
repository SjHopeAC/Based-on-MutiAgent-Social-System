CREATE TABLE IF NOT EXISTS topic_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    topic_id BIGINT NOT NULL COMMENT '话题ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_topic_user (topic_id, user_id) COMMENT '防止重复点赞',
    KEY idx_topic_id (topic_id) COMMENT '话题索引',
    KEY idx_user_id (user_id) COMMENT '用户索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话题点赞记录表';