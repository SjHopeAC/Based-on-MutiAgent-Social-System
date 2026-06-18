package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.User;
import com.example.helloworld.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员Controller
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取用户总数
            int userCount = userMapper.selectCount();
            stats.put("userCount", userCount);
            
            // 获取帖子总数
            int postCount = 0;
            try {
                postCount = userMapper.selectPostCount();
            } catch (Exception e) {
                System.out.println("获取帖子总数失败: " + e.getMessage());
            }
            stats.put("postCount", postCount);
            
            // 获取话题总数
            int topicCount = 0;
            try {
                topicCount = userMapper.selectTopicCount();
            } catch (Exception e) {
                System.out.println("获取话题总数失败: " + e.getMessage());
            }
            stats.put("topicCount", topicCount);
            
            // 获取评论总数
            int commentCount = 0;
            try {
                commentCount = userMapper.selectCommentCount();
            } catch (Exception e) {
                System.out.println("获取评论总数失败: " + e.getMessage());
            }
            stats.put("commentCount", commentCount);
            
            return Result.success("获取统计数据成功", stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取最新用户
     */
    @GetMapping("/recent-users")
    public Result<List<User>> getRecentUsers(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<User> users = userMapper.selectRecentUsers(limit);
            return Result.success("获取最新用户成功", users);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取最新用户失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取热门话题
     */
    @GetMapping("/hot-topics")
    public Result<List<Map<String, Object>>> getHotTopics(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<Map<String, Object>> topics = userMapper.selectHotTopics(limit);
            return Result.success("获取热门话题成功", topics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取热门话题失败：" + e.getMessage());
        }
    }
}
