package com.example.helloworld.service.impl;

import com.example.helloworld.entity.User;
import com.example.helloworld.mapper.UserMapper;
import com.example.helloworld.service.UserFollowService;
import com.example.helloworld.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * UserService实现类（核心业务逻辑在这里）
 */
@Service // 必须加@Service注解，让Spring管理
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper; // 注入Mapper

    @Autowired
    private UserFollowService userFollowService;


        @Override
        public User getUserById(Long userId) {
            return userMapper.selectById(userId);
        }

    @Override//用户登录检验
    public Map<String, Object> login(String username, String password) {
        // 1. 非空校验
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("用户名或密码不能为空");
        }
        // 2. 查询用户
        User user = userMapper.selectUserByUsername(username);
        // 3. 校验用户是否存在
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 新增：检查用户状态（是否被封禁）
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("用户已被封禁");
        }

        // 4. 校验密码
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // ========== 新增：更新最后登录时间 ==========
        try {
            userMapper.updateLastLoginTime(user.getId(), new Date());
        } catch (Exception e) {
            throw new RuntimeException("登录成功，但更新登录时间失败");
        }
        // 5. 构造返回数据（token + userInfo）
        Map<String, Object> data = new HashMap<>();
        // 模拟token
        data.put("token", "token_" + user.getId());

        // 构造userInfo（只返回前端需要的字段）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("gender", user.getGender());
        userInfo.put("birthday", user.getBirthday());
        userInfo.put("bio", user.getBio());
        userInfo.put("balance", user.getBalance());
        userInfo.put("isVip", user.getIsVip());
        userInfo.put("vipExpireTime", user.getVipExpireTime());
        userInfo.put("followCount", user.getFollowCount());
        userInfo.put("fansCount", user.getFansCount());
        userInfo.put("postCount", user.getPostCount());
        userInfo.put("likeCount", user.getLikeCount());
        userInfo.put("commentCount", user.getCommentCount());
        userInfo.put("spamCommentCount", user.getSpamCommentCount());
        userInfo.put("visitCount", user.getVisitCount());
        userInfo.put("isSpamUser", user.getSpamUser());
        userInfo.put("status", user.getStatus());
        userInfo.put("lastLoginTime", user.getLastLoginTime());
        userInfo.put("createTime", user.getCreateTime());
        userInfo.put("updateTime", user.getUpdateTime());


        data.put("userInfo", userInfo);
        return data;
    }

    // 注册核心逻辑
    @Override
    public void register(String username, String email, String password) {
        // 1. 校验用户名是否已存在
        if (userMapper.countByUsername(username) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        // 2. 校验邮箱是否已存在
        if (userMapper.countByEmail(email) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }
        // 3. 构造用户对象
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(username);

        // 4. 插入数据库
        int rows = userMapper.insertUser(user);
        if (rows <= 0) {
            throw new RuntimeException("注册失败，请重试");
        }
    }

    //忘记密码验证手机号和邮箱
    @Override
    public Long verifyUserForResetPwd(String username, String phone, String email) {
        User user = null;
        if (phone != null && !phone.isEmpty()) {
            user = userMapper.selectUserByUsernameAndPhone(username, phone);
        } else if (email != null && !email.isEmpty()) {
            user = userMapper.selectUserByUsernameAndEmail(username, email);
        }

        if (user == null) {
            throw new RuntimeException("用户输入信息不正确，验证不通过");
        }
        return user.getId();
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        int rows = userMapper.updatePassword(userId, newPassword);
        if (rows <= 0) {
            throw new RuntimeException("密码重置失败");
        }
    }

    @Override
    public int getUserCount() {
        return userMapper.selectCount();
    }

    /**
     * 获取活跃用户并附加关注状态（不修改User实体，封装为Map返回）
     * @param currentUserId 当前登录用户ID
     * @return 包含following状态的活跃用户列表
     */
    @Override
    public List<Map<String, Object>> getActiveUsersWithFollowStatus(long currentUserId) {
        // 1. 查询原始活跃用户列表
        List<User> activeUsers = userMapper.selectActiveUsers();
        if (activeUsers == null || activeUsers.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 封装结果：把User转为Map，新增following字段
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (User activeUser : activeUsers) {
            // 2.1 把User对象转为Map（保留所有原有字段）
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", activeUser.getId());
            userMap.put("username", activeUser.getUsername());
            userMap.put("nickname", activeUser.getNickname());
            userMap.put("avatar", activeUser.getAvatar());
            userMap.put("gender", activeUser.getGender());
            userMap.put("bio", activeUser.getBio());
            userMap.put("isVip", activeUser.getIsVip());
            userMap.put("vipExpireTime", activeUser.getVipExpireTime());
            userMap.put("followCount", activeUser.getFollowCount());
            userMap.put("fansCount", activeUser.getFansCount());
            userMap.put("lastLoginTime", activeUser.getLastLoginTime());
            userMap.put("PostCount", activeUser.getPostCount());
            userMap.put("commentCount", activeUser.getCommentCount());
            userMap.put("likeCount", activeUser.getLikeCount());
            userMap.put("spamCommentCount", activeUser.getSpamCommentCount());
            userMap.put("visitCount", activeUser.getVisitCount());
            userMap.put("spamUser", activeUser.getSpamUser());
            userMap.put("status", activeUser.getStatus());
            // 可按需添加User中其他需要返回的字段

            // 2.2 判断当前登录用户是否关注该活跃用户，添加following字段
            boolean isFollowing = userFollowService.isFollowing(currentUserId, activeUser.getId());
            userMap.put("following", isFollowing); // 前端需要的following字段

            resultList.add(userMap);
        }

        return resultList;
    }

    @Override
    public void updateUserInfo(User user) {
        // 校验用户是否存在
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        int rows = userMapper.updateUserInfo(user);
        if (rows <= 0) {
            throw new RuntimeException("更新用户信息失败");
        }
    }

    public void recharge(Long userId, Double amount) {
        // 查询用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户余额
        user.setBalance(user.getBalance().add(BigDecimal.valueOf(amount)));
        userMapper.updateUserBalance(user);
    }

    public void deduct(Long userId, Double amount) {
        // 查询用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 校验余额是否充足
        if (user.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 扣除金额
        user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(amount)));
        userMapper.updateUserBalance(user);
    }
    
    @Override
    public void updateVipStatus(Long userId, String period) {
        // 计算过期时间
        Date vipExpireTime = calculateExpireTime(period);
        
        // 更新VIP状态
        int result = userMapper.updateVipStatus(userId, vipExpireTime);
        if (result <= 0) {
            throw new RuntimeException("更新VIP状态失败");
        }
    }
    
    // 根据套餐有效期计算过期时间
    private Date calculateExpireTime(String period) {
        Date now = new Date();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(now);
        
        if (period.contains("30天")) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 30);
        } else if (period.contains("90天")) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 90);
        } else if (period.contains("365天")) {
            calendar.add(java.util.Calendar.YEAR, 1);
        }
        
        return calendar.getTime();
    }
    
    @Override
    public void incrementPostCount(Long userId) {
        userMapper.incrementPostCount(userId);
    }

    @Override
    public void incrementCommentCount(Long userId) {
        userMapper.incrementCommentCount(userId);
    }

    @Override
    public void incrementSpamCommentCount(Long userId) {
        userMapper.incrementSpamCommentCount(userId);
    }

    @Override
    public void updateSpamUserStatus(Long userId, Boolean isSpamUser) {
        userMapper.updateSpamUserStatus(userId, isSpamUser);
    }


}



