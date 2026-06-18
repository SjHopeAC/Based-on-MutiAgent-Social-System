package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.Topic;
import com.example.helloworld.entity.TopicCategory;
import com.example.helloworld.mapper.TopicMapper;
import com.example.helloworld.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TopicController {
    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicService topicService;

    /**
     * 根据话题ID查询详情
     * 前端访问路径：/api/topic/detail/{topicId}（带context-path:/api）
     */
    @GetMapping("/topic/detail/{topicId}")
    public Result<Topic> getTopicDetail(@PathVariable("topicId") Long topicId,
                                       @RequestParam(value = "currentUserId", required = false) Long currentUserId) {
        try {
            // 1. 参数校验
            if (topicId == null || topicId < 0) {
                return Result.error("话题ID不合法");
            }

            // 2. 查询话题详情
            Topic topic = topicService.getTopicById(topicId);
            if (topic == null) {
                return Result.error("话题不存在或已下架");
            }

            // 3. 可选：查询后更新浏览量（+1）
            topic.setViewCount(topic.getViewCount() + 1);
            topicService.updateViewCount(topic);

            // 4. 检查用户是否点赞了该话题
            if (currentUserId != null) {
                boolean isLiked = topicService.isTopicLiked(topicId, currentUserId);
                topic.setIsLiked(isLiked);
            }

            // 5. 返回结果
            return Result.success("查询成功", topic);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常：" + e.getMessage());
        }
    }

    @GetMapping("/topic")
    public List<Topic> getTopic(@RequestParam(value = "currentUserId", required = false) Long currentUserId,
                                @RequestParam(value = "isHot", required = false) Integer isHot,
                                @RequestParam(value = "orderBy", required = false) String orderBy,
                                @RequestParam(value = "followed", required = false) Integer followed){
        List<Topic> topics;
        
        if (followed != null && followed == 1) {
            topics = topicMapper.selectFollowedTopics(currentUserId);
        } else if (isHot != null && isHot == 1) {
            topics = topicMapper.selectHotTopics();
        } else if ("createTime".equals(orderBy)) {
            topics = topicMapper.selectNewestTopics();
        } else {
            topics = topicMapper.selectAllTopics();
        }
        
        // 更新每个话题的participantCount（评论数总和）
        for (Topic topic : topics) {
            topicService.updateParticipantCountByCommentCount(topic.getTopicId());
        }
        
        // 重新查询更新后的话题数据
        if (followed != null && followed == 1) {
            topics = topicMapper.selectFollowedTopics(currentUserId);
        } else if (isHot != null && isHot == 1) {
            topics = topicMapper.selectHotTopics();
        } else if ("createTime".equals(orderBy)) {
            topics = topicMapper.selectNewestTopics();
        } else {
            topics = topicMapper.selectAllTopics();
        }
        
        if (currentUserId != null) {
            for (Topic topic : topics) {
                boolean isLiked = topicService.isTopicLiked(topic.getTopicId(), currentUserId);
                topic.setIsLiked(isLiked);
            }
        }
        
        return topics;
    }
    
    @GetMapping("/topics")
    public Map<String, Object> getTopics(@RequestParam(value = "currentUserId", required = false) Long currentUserId,
                                        @RequestParam(value = "isHot", required = false) Integer isHot,
                                        @RequestParam(value = "orderBy", required = false) String orderBy,
                                        @RequestParam(value = "followed", required = false) Integer followed,
                                        @RequestParam(value = "tag", required = false) String tag){
        List<Topic> topics;
        
        if (followed != null && followed == 1) {
            topics = topicMapper.selectFollowedTopics(currentUserId);
        } else if (isHot != null && isHot == 1) {
            topics = topicMapper.selectHotTopics();
        } else if ("createTime".equals(orderBy)) {
            topics = topicMapper.selectNewestTopics();
        } else {
            topics = topicMapper.selectAllTopics();
        }
        
        // 更新每个话题的participantCount（评论数总和）
        for (Topic topic : topics) {
            topicService.updateParticipantCountByCommentCount(topic.getTopicId());
        }
        
        // 重新查询更新后的话题数据
        if (followed != null && followed == 1) {
            topics = topicMapper.selectFollowedTopics(currentUserId);
        } else if (isHot != null && isHot == 1) {
            topics = topicMapper.selectHotTopics();
        } else if ("createTime".equals(orderBy)) {
            topics = topicMapper.selectNewestTopics();
        } else {
            topics = topicMapper.selectAllTopics();
        }
        
        if (currentUserId != null) {
            for (Topic topic : topics) {
                boolean isLiked = topicService.isTopicLiked(topic.getTopicId(), currentUserId);
                topic.setIsLiked(isLiked);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", topics);
        return result;
    }
    
    @GetMapping("/topics/trending")
    public Map<String, Object> getTrendingTopics(){
        List<Topic> topics = topicMapper.selectHotTopics();
        Map<String, Object> result = new HashMap<>();
        result.put("data", topics);
        return result;
    }


    /**
     * 创建话题接口（对接前端 /topic/create）
     * @param topic 前端传入的话题参数
     * @return 统一返回结果
     */
    @PostMapping("/topic/create")
    public Result<Topic> createTopic(@RequestBody Topic topic) {
        try {
            // 仅校验数据库非空/外键相关字段（其余靠数据库默认值）
            if (topic.getCategoryId() == null) {
                return Result.error("话题分类ID不能为空");
            }
            if (topic.getTopicTitle() == null || topic.getTopicTitle().trim().isEmpty()) {
                return Result.error("话题标题不能为空");
            }
            if (topic.getCreatorId() == null) {
                return Result.error("创建者ID不能为空（请先登录）");
            }

            // 🔥 核心：不再手动赋值任何字段！数据库自动处理默认值
            // 删掉所有setViewCount/setIsHot/setCreateTime等代码

            boolean success = topicService.createTopic(topic);
            if (success) {
                return Result.success("话题发布成功", topic);
            } else {
                return Result.error("话题发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常：" + e.getMessage());
        }
    }

    /**
     * 更新话题点赞数接口
     * @param topicId 话题ID
     * @param count 点赞变化量（正数表示增加，负数表示减少）
     * @param currentUserId 当前用户ID
     * @return 统一返回结果
     */
    @PostMapping("/topic/like")
    public Result<String> updateTopicLike(@RequestParam("topicId") Long topicId,
                                          @RequestParam("count") Integer count,
                                          @RequestParam(value = "currentUserId", required = false) Long currentUserId) {
        try {
            if (currentUserId == null) {
                return Result.error("请先登录后再点赞");
            }
            
            boolean success = topicService.updateTopicLikeCount(topicId, count, currentUserId);
            if (success) {
                return Result.success("点赞更新成功");
            } else {
                return Result.error("点赞更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户话题列表接口
     * @param userId 用户ID
     * @return 用户话题列表
     */
    @GetMapping("/topic/user/{userId}")
    public Result<List<Topic>> getUserTopics(@PathVariable("userId") Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return Result.error("用户ID不合法");
            }
            
            List<Topic> userTopics = topicService.getUserTopics(userId);
            return Result.success("获取用户话题列表成功", userTopics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常：" + e.getMessage());
        }
    }
}
