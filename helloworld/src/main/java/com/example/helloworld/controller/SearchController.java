package com.example.helloworld.controller;

import com.example.helloworld.entity.Post;
import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.Topic;
import com.example.helloworld.entity.User;
import com.example.helloworld.mapper.PostMapper;
import com.example.helloworld.mapper.TopicMapper;
import com.example.helloworld.mapper.UserMapper;
import com.example.helloworld.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFollowService userFollowService;

    /**
     * 综合搜索（话题、帖子、用户）
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<Map<String, Object>> search(@RequestParam String keyword,
                                             @RequestParam(value = "currentUserId", required = false) Long currentUserId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }

        Map<String, Object> result = new HashMap<>();
        
        // 搜索话题
        List<Topic> topics = topicMapper.searchTopics(keyword);
        
        // 搜索帖子
        List<Post> posts = postMapper.searchPosts(keyword);
        
        // 搜索用户
        List<User> users = userMapper.searchUsers(keyword);
        
        // 如果有当前用户ID，检查关注状态
        if (currentUserId != null && users != null && !users.isEmpty()) {
            for (User user : users) {
                boolean isFollowing = userFollowService.isFollowing(currentUserId, user.getId());
                user.setFollowing(isFollowing);
            }
        }
        
        result.put("topics", topics);
        result.put("posts", posts);
        result.put("users", users);
        
        return Result.success("搜索成功", result);
    }

    /**
     * 搜索话题
     */
    @GetMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<List<Topic>> searchTopics(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        List<Topic> topics = topicMapper.searchTopics(keyword);
        return Result.success("搜索成功", topics);
    }

    /**
     * 搜索帖子
     */
    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<List<Post>> searchPosts(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        List<Post> posts = postMapper.searchPosts(keyword);
        return Result.success("搜索成功", posts);
    }

    /**
     * 搜索用户
     */
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        List<User> users = userMapper.searchUsers(keyword);
        return Result.success("搜索成功", users);
    }

    /**
     * 获取热门搜索选项（热门话题、热门标签）
     */
    @GetMapping(value = "/hot", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<Map<String, Object>> getHotSearchOptions() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取热门话题
        List<Topic> hotTopics = topicMapper.selectTrendingTopics();
        
        // 获取所有标签
        List<String> tags = topicMapper.selectAllTags();
        
        result.put("hotTopics", hotTopics);
        result.put("hotTags", tags);
        
        return Result.success("获取热门搜索成功", result);
    }
}
