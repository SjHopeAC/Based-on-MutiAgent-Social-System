package com.example.helloworld.service.impl;

import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.Topic;
import com.example.helloworld.entity.TopicCategory;
import com.example.helloworld.entity.TopicLike;
import com.example.helloworld.entity.Post;
import com.example.helloworld.mapper.TopicCategoryMapper;
import com.example.helloworld.mapper.TopicMapper;
import com.example.helloworld.mapper.TopicLikeMapper;
import com.example.helloworld.mapper.PostMapper;

import com.example.helloworld.service.TopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl implements TopicService {

    @Resource
    private TopicMapper topicMapper;

    @Resource
    private TopicCategoryMapper topicCategoryMapper;

    @Resource
    private TopicLikeMapper topicLikeMapper;
    
    @Resource
    private PostMapper postMapper;


    // 新增：查询话题详情
    @Override
    public Topic getTopicById(Long topicId) {
        // 调用Mapper查询，返回null表示未找到
        return topicMapper.selectTopicById(topicId);
    }

    /**
     * 创建话题（添加事务注解，确保数据一致性）
     * @param topic 话题对象
     * @return 是否创建成功
     */
    @Override
    @Transactional
    public boolean createTopic(Topic topic) {
        // 调用注解版Mapper，返回受影响行数
        int affectedRows = topicMapper.insertTopic(topic);
        
        // 如果创建成功，更新分类表中的话题数
        if (affectedRows > 0 && topic.getCategoryId() != null) {
            topicCategoryMapper.incrementTopicCount(topic.getCategoryId());
        }
        
        return affectedRows > 0; // 行数>0=插入成功
    }

    @Override
    public Result<List<Topic>> getTopics(String filter, Integer page, Integer size) {
        // 处理默认值
        if (page == null) page = 1;
        if (size == null) size = 10;
        int offset = (page - 1) * size;

        List<Topic> topics = topicMapper.selectTopicsByFilter(filter, offset, size);
        return Result.success(topics);
    }

    @Override
    public Result<?> likeTopic(Long topicId) {
        int result = topicMapper.incrementLikeCount(topicId);
        if (result > 0) {
            return Result.success("点赞成功");
        } else {
            return Result.error("点赞失败");
        }
    }

    @Override
    public Result<?> viewTopic(Long topicId) {
        int result = topicMapper.incrementViewCount(topicId);
        if (result > 0) {
            return Result.success("浏览量更新成功");
        } else {
            return Result.error("浏览量更新失败");
        }
    }

    /**
     * 重构热门话题查询逻辑：
     * 1. 计算话题热度值，筛选TOP5
     * 2. 更新is_hot标记
     * 3. 查询is_hot=1的话题返回
     */
    @Override
    public Result<List<Topic>> getTrendingTopics() {
        // 步骤1：先重置所有正常话题的is_hot为0（清空历史热门标记）
        topicMapper.resetAllHotStatus();

        // 步骤2：按热度规则筛选正常状态的TOP5话题（热度值=浏览*0.2+点赞*0.5+参与*0.3，同值按更新时间降序）
        List<Topic> top5HotTopics = topicMapper.selectTop5HotTopicsByScore();

        // 步骤3：仅把筛选出的TOP5标记为热门（is_hot=1）
        for (Topic topic : top5HotTopics) {
            topicMapper.updateSingleHotStatus(topic.getTopicId());
        }

        // 步骤4：查询最终标记为热门的话题返回（is_hot=1+status=1）
        List<Topic> hotTopics = topicMapper.selectHotTopicsByIsHotFlag();

        // 兜底：无数据返回空列表，避免NullPointerException
        List<Topic> result = hotTopics != null ? hotTopics : new ArrayList<>();

        return Result.success(result);
    }


    @Override
    public List<TopicCategory> getHotTopicCategories() {
        // 从数据库查询分类列表
        return topicCategoryMapper.getHotTopicCategories();
    }

    @Override
    public int getTopicCount() {
        return topicMapper.selectCount();
    }

    @Override
    public List<Topic> getTopics() {
        return topicMapper.selectList(null);
    }

    @Override
    public List<Topic> getTrendingTopicsSimple() {
        return topicMapper.selectTrendingTopics();
    }

    @Override
    public List<String> getPopularTags() {
        // 从数据库查询所有标签
        List<String> tagStrings = topicMapper.selectAllTags();
        
        // 统计标签出现的次数
        Map<String, Integer> tagCountMap = new HashMap<>();
        for (String tagString : tagStrings) {
            if (tagString != null && !tagString.isEmpty()) {
                String[] tags = tagString.split(",");
                for (String tag : tags) {
                    tag = tag.trim();
                    if (!tag.isEmpty()) {
                        tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0) + 1);
                    }
                }
            }
        }
        
        // 排序并返回热门标签
        List<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(tagCountMap.entrySet());
        sortedTags.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        List<String> popularTags = new ArrayList<>();
        for (int i = 0; i < Math.min(10, sortedTags.size()); i++) {
            popularTags.add(sortedTags.get(i).getKey());
        }
        
        // 如果没有标签，返回默认标签
        if (popularTags.isEmpty()) {
            popularTags.add("目前还没有热门标签哦，快来发布话题吧！");
        }
        
        return popularTags;
    }

    //话题浏览量+1
    @Override
    public void updateViewCount(Topic topic) {
        topicMapper.incrementViewCount(topic.getTopicId());
    }

    /**
     * 更新话题点赞数
     * @param topicId 话题ID
     * @param count 点赞变化量（正数表示增加，负数表示减少）
     * @param userId 用户ID
     * @return 是否更新成功
     */
    @Override
    @Transactional
    public boolean updateTopicLikeCount(Long topicId, Integer count, Long userId) {
        if (topicId == null || count == null) {
            throw new IllegalArgumentException("话题ID或点赞变化量不能为空");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 更新话题的点赞数
        int rowsAffected = topicMapper.updateLikeCount(topicId, count);
        
        if (rowsAffected > 0) {
            // 持久化点赞记录
            if (count > 0) {
                // 点赞：先检查是否已经点赞，避免重复插入
                int existingCount = topicLikeMapper.countTopicLike(topicId, userId);
                if (existingCount == 0) {
                    TopicLike topicLike = new TopicLike();
                    topicLike.setTopicId(topicId);
                    topicLike.setUserId(userId);
                    topicLikeMapper.insertTopicLike(topicLike);
                }
            } else {
                // 取消点赞：删除点赞记录
                topicLikeMapper.deleteTopicLike(topicId, userId);
            }
        }
        
        return rowsAffected > 0;
    }
    
    @Override
    public List<Topic> getUserTopics(Long userId) {
        return topicMapper.getTopicByUserId(userId);
    }
    
    @Override
    public boolean isTopicLiked(Long topicId, Long userId) {
        if (topicId == null || userId == null) {
            return false;
        }
        int count = topicLikeMapper.countTopicLike(topicId, userId);
        return count > 0;
    }
    
    @Override
    public List<Topic> getTopicsByFollowed(Long currentUserId) {
        return topicMapper.selectFollowedTopics(currentUserId);
    }
    
    @Override
    public List<Topic> getTopicsByHot() {
        return topicMapper.selectHotTopics();
    }
    
    @Override
    public List<Topic> getTopicsByNewest() {
        return topicMapper.selectNewestTopics();
    }
    
    @Override
    public void updateParticipantCountByCommentCount(Long topicId) {
        if (topicId == null) {
            return;
        }
        
        // 查询该话题下所有帖子
        List<Post> posts = postMapper.selectPostsByTopicId(topicId);
        
        // 计算评论数总和
        int totalCommentCount = 0;
        if (posts != null && !posts.isEmpty()) {
            totalCommentCount = posts.stream()
                .filter(post -> post.getCommentCount() != null)
                .mapToInt(Post::getCommentCount)
                .sum();
        }
        
        // 更新话题的participantCount
        topicMapper.updateParticipantCount(topicId, totalCommentCount);
    }
    
    @Override
    public void updateAllTopicsParticipantCount() {
        // 获取所有话题
        List<Topic> topics = topicMapper.selectAllTopics();
        
        if (topics != null && !topics.isEmpty()) {
            for (Topic topic : topics) {
                updateParticipantCountByCommentCount(topic.getTopicId());
            }
        }
    }
}

