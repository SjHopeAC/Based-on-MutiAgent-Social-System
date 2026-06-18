package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 关注关系Controller
 */
@RestController
@RequestMapping("/follow")
public class FollowController {
    
    @Autowired
    private UserFollowService userFollowService;
    
    /**
     * 检查关注状态
     * @param userId 当前用户ID
     * @param targetUserId 目标用户ID
     * @return 是否已关注
     */
    @GetMapping("/check")
    public Result<Boolean> checkFollowStatus(
            @RequestParam Long userId,
            @RequestParam Long targetUserId) {
        try {
            boolean isFollowing = userFollowService.isFollowing(userId, targetUserId);
            return Result.success("查询成功", isFollowing);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 关注用户
     * @param params 包含userId和targetUserId
     * @return 操作结果
     */
    @PostMapping("/follow")
    public Result<String> follow(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.parseLong(params.get("userId").toString());
            Long targetUserId = Long.parseLong(params.get("targetUserId").toString());
            
            boolean success = userFollowService.toggleFollow(userId, targetUserId);
            if (success) {
                return Result.success("关注成功", "关注成功");
            } else {
                return Result.error("关注失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("关注失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消关注
     * @param params 包含userId和targetUserId
     * @return 操作结果
     */
    @PostMapping("/unfollow")
    public Result<String> unfollow(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.parseLong(params.get("userId").toString());
            Long targetUserId = Long.parseLong(params.get("targetUserId").toString());
            
            boolean success = userFollowService.toggleFollow(userId, targetUserId);
            if (success) {
                return Result.success("取消关注成功", "取消关注成功");
            } else {
                return Result.error("取消关注失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("取消关注失败：" + e.getMessage());
        }
    }
}
