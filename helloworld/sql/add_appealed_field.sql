-- 添加appealed字段到post_comment表
ALTER TABLE post_comment ADD COLUMN appealed TINYINT(1) DEFAULT 0 COMMENT '是否已申诉：0-未申诉，1-已申诉';

-- 更新现有记录的appealed字段为0
UPDATE post_comment SET appealed = 0 WHERE appealed IS NULL;
