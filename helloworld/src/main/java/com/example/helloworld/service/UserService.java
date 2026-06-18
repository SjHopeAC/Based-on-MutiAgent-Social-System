package com.example.helloworld.service;

import com.example.helloworld.entity.User;
import java.util.List;
import java.util.Map;

/**
 * 用户服务接口（定义登录等业务方法）
 */
public interface UserService {
    //登录校验
    Map<String, Object> login(String username, String password);

    // 新增：注册方法
    void register(String username, String email, String password);

    // 忘记密码验证（手机/邮箱）
    Long verifyUserForResetPwd(String username, String phone, String email);

    // 重置密码
    void resetPassword(Long userId, String newPassword);

    // 获取用户总数
    int getUserCount();

    User getUserById(Long userId);

    // 获取活跃用户列表
    List<Map<String, Object>> getActiveUsersWithFollowStatus(long currentUserId);

    // 更新用户信息
    void updateUserInfo(User user);


    //更改账户余额
    public void recharge(Long userId, Double amount);
    public void deduct(Long userId, Double amount);
    
    //增加用户发帖数
    void incrementPostCount(Long userId);

    // 增加用户评论数
    void incrementCommentCount(Long userId);

    // 增加用户垃圾评论数
    void incrementSpamCommentCount(Long userId);

    // 更新用户劣迹状态
    void updateSpamUserStatus(Long userId, Boolean isSpamUser);

    // 更新用户VIP状态
    void updateVipStatus(Long userId, String period);
}