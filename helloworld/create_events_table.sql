CREATE TABLE IF NOT EXISTS events (
     event_id VARCHAR(32) PRIMARY KEY COMMENT '事件ID',
     core_entity VARCHAR(200) NOT NULL COMMENT '核心实体',
     event_type VARCHAR(50) NOT NULL COMMENT '事件类型',
     keywords JSON NOT NULL COMMENT '关键词列表',
     summary VARCHAR(500) NOT NULL COMMENT '事件摘要',
     total_influence DECIMAL(15,2) DEFAULT 0 COMMENT '总影响力',
     first_time DATETIME NOT NULL COMMENT '首次发现时间',
     last_time DATETIME NOT NULL COMMENT '最后更新时间',
     status VARCHAR(20) DEFAULT '关注' COMMENT '状态',
     severity_level TINYINT DEFAULT 1 COMMENT '严重程度'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='舆情事件表';
