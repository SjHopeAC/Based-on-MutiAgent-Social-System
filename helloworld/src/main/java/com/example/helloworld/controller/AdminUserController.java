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
 * 管理员用户管理Controller
 */
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            int offset = (page - 1) * size;
            
            List<User> users;
            int total;
            
            if (keyword != null && !keyword.isEmpty()) {
                users = userMapper.searchUsers(keyword);
                total = users.size();
            } else {
                users = userMapper.getAllUser();
                total = users.size();
            }
            
            // 分页处理
            int fromIndex = Math.min(offset, users.size());
            int toIndex = Math.min(offset + size, users.size());
            List<User> pageList = users.subList(fromIndex, toIndex);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageList);
            result.put("total", total);
            
            return Result.success("获取用户列表成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 切换用户状态（启用/禁用）
     */
    @PutMapping("/{userId}/status")
    public Result<String> toggleUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> params) {
        try {
            int status = params.get("status");
            int result = userMapper.updateUserStatus(userId, status);
            if (result > 0) {
                return Result.success("更新用户状态成功", "更新成功");
            } else {
                return Result.error("更新用户状态失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新用户状态失败：" + e.getMessage());
        }
    }
}
