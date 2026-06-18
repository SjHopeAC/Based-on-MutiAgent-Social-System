package com.example.helloworld.service;

import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.Topic;
import com.example.helloworld.entity.TopicCategory;

import java.util.List;

public interface TopicService {
    // 分页查询话题
    Result<List<Topic>> getTopics(String filter, Integer page, Integer size);

    // 新增：根据ID查询话题详情
    Topic getTopicById(Long topicId);

    // 点赞
    Result<?> likeTopic(Long topicId);

    // 浏览量+1
    Result<?> viewTopic(Long topicId);

    // 获取热门话题
    Result<List<Topic>> getTrendingTopics();

    // 获取分类列表
    List<TopicCategory> getHotTopicCategories();

    // 获取总话题数
    int getTopicCount();

    // 获取话题列表（简单版）
    List<Topic> getTopics();

    // 获取热门话题（简单版）
    List<Topic> getTrendingTopicsSimple();

    // 获取热门标签
    List<String> getPopularTags();

    /**
     * 创建话题
     * @param topic 话题对象
     * @return 是否创建成功
     */
    boolean createTopic(Topic topic);

    // 浏览量+1
    void updateViewCount(Topic topic);

    // 点赞数+1
    boolean updateTopicLikeCount(Long topicId, Integer count, Long userId);
    
    // 获取用户话题列表
    List<Topic> getUserTopics(Long userId);
    
    // 检查用户是否点赞了话题
    boolean isTopicLiked(Long topicId, Long userId);
    
    // 获取关注的话题列表
    List<Topic> getTopicsByFollowed(Long currentUserId);
    
    // 获取热门话题列表
    List<Topic> getTopicsByHot();
    
    // 获取最新话题列表
    List<Topic> getTopicsByNewest();
    
    // 计算话题下所有帖子的评论数总和，并更新到participantCount字段
    void updateParticipantCountByCommentCount(Long topicId);
    
    // 批量更新所有话题的participantCount（评论数总和）
    void updateAllTopicsParticipantCount();
}