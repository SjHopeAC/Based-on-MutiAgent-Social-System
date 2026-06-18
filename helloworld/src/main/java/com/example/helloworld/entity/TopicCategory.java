package com.example.helloworld.entity;

import lombok.Data;

/**
 * 话题分类实体类
 * 对应表：topic_category
 */
@Data
public class TopicCategory {
    /**
     * 分类ID（主键自增）
     */
    private Integer categoryId;

    /**
     * 分类名称（唯一）
     */
    private String categoryName;

    /**
     * 是否热门分类：1-热门，0-非热门
     */
    private Integer isHot;

    /**
     * 分类下话题数量
     */
    private Integer topicCount;
}