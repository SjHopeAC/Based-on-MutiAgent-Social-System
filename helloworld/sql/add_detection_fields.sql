-- 添加垃圾评论检测结果字段到post_comment表
ALTER TABLE post_comment ADD COLUMN confidence DECIMAL(10,3) DEFAULT NULL COMMENT '置信度，保留3位小数';
ALTER TABLE post_comment ADD COLUMN reasoning VARCHAR(1024) DEFAULT NULL COMMENT '分析推理说明';
ALTER TABLE post_comment ADD COLUMN result VARCHAR(32) DEFAULT NULL COMMENT '检测结果';
ALTER TABLE post_comment ADD COLUMN source VARCHAR(64) DEFAULT NULL COMMENT '检测来源';

-- 更新现有记录的检测结果字段为NULL
UPDATE post_comment SET confidence = NULL, reasoning = NULL, result = NULL, source = NULL WHERE confidence IS NULL;