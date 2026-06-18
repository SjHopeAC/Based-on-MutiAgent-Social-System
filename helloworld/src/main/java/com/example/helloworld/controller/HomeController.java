package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.Topic;
import com.example.helloworld.entity.TopicCategory;
import com.example.helloworld.entity.User;
import com.example.helloworld.mapper.UserFollowMapper;
import com.example.helloworld.service.TopicService;
import com.example.helloworld.service.UserFollowService;
import com.example.helloworld.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFollowService userFollowService; // 注入Mapper

    /**
     * 获取系统统计数据
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> stats = new ArrayList<>();
        
        // 注册用户数
        Map<String, String> userStat = new HashMap<>();
        userStat.put("label", "注册用户");
        userStat.put("value", String.valueOf(userService.getUserCount()));
        stats.add(userStat);
        
        // 在线用户数（模拟数据）
        Map<String, String> onlineStat = new HashMap<>();
        onlineStat.put("label", "在线用户");
        onlineStat.put("value", "##");
        stats.add(onlineStat);
        
        // 总话题数
        Map<String, String> topicStat = new HashMap<>();
        topicStat.put("label", "总话题数");
        topicStat.put("value", String.valueOf(topicService.getTopicCount()));
        stats.add(topicStat);
        
        result.put("data", stats);
        return result;
    }

    /**
     * 获取活跃用户
     */
    @GetMapping("/users/active")
    public Map<String, Object> getActiveUsers(@RequestParam("currentUserId") Long currentUserId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> activeUsers = userService.getActiveUsersWithFollowStatus(currentUserId);
        result.put("data", activeUsers);
        return result;
    }

    /**
     * 关注/取消关注用户接口
     * @param userId 被关注/取消关注的用户ID（路径参数）
     * @param currentUserId 当前登录用户ID（请求参数）
     * @return 操作结果（包含异常信息）
     */
    @PostMapping("/users/{userId}/follow")
    public Map<String, Object> followUser(
            @PathVariable("userId") Long userId, // 被关注人ID
            @RequestParam("currentUserId") Long currentUserId) { // 当前登录用户ID

        Map<String, Object> result = new HashMap<>();
        try {
            // 调用service处理关注/取消关注逻辑
            boolean operateSuccess = userFollowService.toggleFollow(currentUserId, userId);
            result.put("code", 200);
            result.put("message", "操作成功");
            result.put("success", true);
        } catch (RuntimeException e) { // 捕获Service层抛出的运行时异常（包括“不能关注自己”）
            // 封装异常信息返回给前端
            result.put("code", 400); // 400表示参数/业务逻辑错误
            result.put("message", e.getMessage()); // 直接返回“不能关注自己”
            result.put("success", false);
            // 可选：打印异常栈（方便后端排查，生产环境可关闭）
            e.printStackTrace();
        } catch (Exception e) { // 捕获其他未知异常
            result.put("code", 500);
            result.put("message", "服务器内部错误");
            result.put("success", false);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/tags/popular")
    public Map<String, Object> getPopularTags() {
        Map<String, Object> result = new HashMap<>();
        List<String> popularTags = topicService.getPopularTags();
        result.put("data", popularTags);
        return result;
    }
}
