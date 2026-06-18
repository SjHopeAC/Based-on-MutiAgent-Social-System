package com.example.helloworld.controller;

import com.example.helloworld.entity.Post;
import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.User;
import com.example.helloworld.service.PostService;
import com.example.helloworld.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/spam")
public class SpamController {

    @Autowired
    private PostService postService;
    
    @Autowired
    private UserService userService;

    // 获取环境上下文（帖子相关信息）
    @GetMapping("/post/{postId}")
    public Result<Map<String, String>> getEnvironmentContext(@PathVariable String postId) {
        try {
            Map<String, String> result = new HashMap<>();
            
            // 尝试将postId转换为Long
            try {
                Long postIdLong = Long.parseLong(postId);
                // 从数据库获取帖子信息
                Post post = postService.getPostById(postIdLong, null);
                if (post != null) {
                    // 构建环境上下文信息
                    String[] observations = {
                        "帖子标题：" + (post.getPostContent() != null ? post.getPostContent().substring(0, Math.min(60, post.getPostContent().length())) + "..." : "无标题"),
                        "帖子内容：" + (post.getPostContent() != null ? post.getPostContent().substring(0, Math.min(200, post.getPostContent().length())) + "..." : "无内容")
                    };
                    String observation = String.join("；", observations);
                    result.put("observation", observation);
                } else {
                    result.put("observation", "帖子不存在");
                }
            } catch (NumberFormatException e) {
                result.put("observation", "无效的帖子ID");
            }
            
            return Result.success("获取环境上下文成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取环境上下文失败：" + e.getMessage());
        }
    }

    // 获取用户行为（用户相关信息）
    @GetMapping("/user/{userId}")
    public Result<Map<String, String>> getUserBehavior(@PathVariable String userId) {
        try {
            Map<String, String> result = new HashMap<>();
            
            // 尝试从userId中提取数字部分（去掉"user_"前缀）
            String userIdStr = userId;
            if (userId.startsWith("user_")) {
                userIdStr = userId.substring(5);
            }
            
            try {
                Long userIdLong = Long.parseLong(userIdStr);
                // 从数据库获取用户信息
                User user = userService.getUserById(userIdLong);
                if (user != null) {
                    // 计算账号创建天数
                    long daysSinceCreation = 0;
                    if (user.getCreateTime() != null) {
                        daysSinceCreation = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - user.getCreateTime().getTime());
                    }
                    
                    // 构建用户行为信息
                    String[] observations = {
                        "劣迹评论数：" + (user.getSpamCommentCount() != null ? user.getSpamCommentCount() : 0),
                        "总评论数：" + (user.getCommentCount() != null ? user.getCommentCount() : 0),
                        "账号已创建：" + daysSinceCreation + "天",
                        "是否劣迹用户：" + (user.getSpamUser() != null && user.getSpamUser() ? "是" : "否")
                    };
                    String observation = String.join("；", observations);
                    result.put("observation", observation);
                } else {
                    result.put("observation", "用户不存在");
                }
            } catch (NumberFormatException e) {
                result.put("observation", "无效的用户ID");
            }
            
            return Result.success("获取用户行为成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户行为失败：" + e.getMessage());
        }
    }
}
