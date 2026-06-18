package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.service.UserService;
import com.example.helloworld.service.UserFollowService;
import com.example.helloworld.entity.User;
import com.example.helloworld.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService; // 注入Service
    @Autowired
    private UserFollowService userFollowService; // 注入UserFollowService

    // 头像上传的基础路径（项目根目录下的upload）
    private String path = System.getProperty("user.dir") + File.separator + "upload" + File.separator;

    //注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> registerParam) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取前端传的注册参数
            String username = registerParam.get("username");
            String email = registerParam.get("email");
            String password = registerParam.get("password");
            // 前端已校验确认密码，后端不用重复校验

            // 2. 调用Service注册
            userService.register(username, email, password);

            // 3. 注册成功响应
            result.put("code", 200);
            result.put("msg", "注册成功");
            result.put("data", null);

        } catch (RuntimeException e) {
            // 4. 业务异常（用户名/邮箱已存在）
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            // 5. 其他异常
            result.put("code", 500);
            result.put("msg", "注册失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    //登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginParam) {
        // 1. 定义返回结果（严格匹配前端格式）
        Map<String, Object> result = new HashMap<>();

        try {
            // 2. 获取前端参数
            String username = loginParam.get("username");
            String password = loginParam.get("password");
            System.out.println("username: " + username);
            System.out.println("password: " + password);

            // 3. 核心：调用Service层处理登录逻辑
            Map<String, Object> data = userService.login(username, password);

            // 4. 构造成功响应
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("data", data);

        } catch (RuntimeException e) {
            // 5. 捕获Service层抛出的异常，构造失败响应
            result.put("code", 500);
            result.put("msg", e.getMessage()); // 直接返回Service层的错误信息
            result.put("data", null);
        } catch (Exception e) {
            // 6. 捕获其他异常
            result.put("code", 500);
            result.put("msg", "登录失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    // 忘记密码验证接口
    @PostMapping("/forgot-password/verify")
    public Map<String, Object> forgotPasswordVerify(@RequestBody Map<String, String> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = param.get("username");
            String phone = param.get("phone");
            String email = param.get("email");

            Long userId = userService.verifyUserForResetPwd(username, phone, email);
            result.put("code", 200);
            result.put("msg", "验证成功");
            result.put("data", userId);
        } catch (RuntimeException e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "验证失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    // 重置密码接口
    @PostMapping("/forgot-password/reset")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.parseLong(param.get("userId"));
            String newPassword = param.get("newPassword");

            userService.resetPassword(userId, newPassword);
            result.put("code", 200);
            result.put("msg", "密码重置成功");
            result.put("data", null);
        } catch (RuntimeException e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "重置失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    @GetMapping("/user/{userId}")
    public Result<User> getUserById(@PathVariable Long userId) {
        try {
            // 调用 Service 层获取用户信息
            User user = userService.getUserById(userId);
            if (user != null) {
                return Result.success("获取用户信息成功",user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/user")
    public List<User> getUser(){
        return userMapper.getAllUser();
    }

    @GetMapping("/user/findAll")
    public List<User> getUserById(){
        System.out.println("getUsersAndTopics");
        return userMapper.getAllUsersAndTopics();
    }

    /**
     * 更新用户头像接口
     * @param username 用户名（唯一标识用户）
     * @param avatar 新的头像文件
     * @return 操作结果
     */
    @PostMapping("/upload/avatar")
    public String updateUserAvatar(
            @RequestParam("username") String username,  // 用户名（定位用户）
            @RequestParam("avatar") MultipartFile avatar // 新头像文件
    ) {
        try {
            // 1. 基础参数校验
            if (username == null || username.trim().isEmpty()) {
                return "用户名不能为空";
            }
            if (avatar == null || avatar.isEmpty()) {
                return "请选择要上传的头像文件";
            }

            // 2. 处理头像文件：和插入接口保持相同的命名规则（username + 原后缀）
            // 获取文件后缀（处理无后缀的情况）
            String originalFilename = avatar.getOriginalFilename();
            String suffix = originalFilename.lastIndexOf(".") > 0
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".png"; // 无后缀默认png

            // 新文件名（覆盖原同名文件，实现更新）
            String fileName = username + suffix;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs(); // 目录不存在则创建
            }

            // 3. 上传文件（覆盖原有头像）
            avatar.transferTo(new File(path + fileName));

            // 4. 更新数据库中的头像路径
            String avatarPath = "/" + fileName; // 和插入接口保持相同的路径格式
            int updateCount = userMapper.updateUserAvatar(username, avatarPath);

            // 5. 返回结果
            return updateCount > 0 ? "头像更新成功" : "头像更新失败（用户不存在）";

        } catch (IOException e) {
            return "头像上传失败：" + e.getMessage();
        } catch (Exception e) {
            return "头像更新失败：" + e.getMessage();
        }
    }


    @PostMapping("/user")
    public String insertUser(
            @RequestParam("username") String username,  // 用户名
            @RequestParam("password") String password,  // 密码
            @RequestParam("nickname") String nickname,  // 昵称
            @RequestParam("avatar") MultipartFile image   // 头像文件
    ) {
        try {
            // 1. 处理头像上传：文件名改为username + 原后缀
            String suffix = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
            String fileName = username + suffix;
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            image.transferTo(new File(path + fileName));

            // 2. 组装用户信息（仅核心字段）
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname);
            user.setAvatar("/" + fileName);  // 头像访问路径

            // 3. 插入用户
            int i = userMapper.insertUser(user);
            return i > 0 ? "插入成功" : "插入失败";

        } catch (IOException e) {
            return "上传失败：" + e.getMessage();
        } catch (Exception e) {
            return "插入失败：" + e.getMessage();
        }
    }

    /**
     * 更新用户信息接口
     * @param user 用户信息
     * @return 操作结果
     */
    @PostMapping("/user/update")
    public Map<String, Object> updateUserInfo(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用Service更新用户信息
            userService.updateUserInfo(user);

            // 更新成功响应
            result.put("code", 200);
            result.put("msg", "更新用户信息成功");
            result.put("data", null);

        } catch (RuntimeException e) {
            // 业务异常
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            // 其他异常
            result.put("code", 500);
            result.put("msg", "更新用户信息失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 用户充值接口
     * @param param 包含 userId 和 amount 的请求参数
     * @return 操作结果
     */
    @PostMapping("/user/recharge")
    public Map<String, Object> recharge(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取参数
            Long userId = Long.parseLong(param.get("userId").toString());
            Double amount = Double.parseDouble(param.get("amount").toString());

            // 2. 参数校验
            if (userId <= 0 || amount <= 0) {
                result.put("code", 400);
                result.put("msg", "参数不合法");
                result.put("data", null);
                return result;
            }

            // 3. 调用 Service 执行充值逻辑
            userService.recharge(userId, amount);

            // 4. 成功响应
            result.put("code", 200);
            result.put("msg", "充值成功");
            result.put("data", null);
        } catch (RuntimeException e) {
            // 业务异常
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            // 其他异常
            result.put("code", 500);
            result.put("msg", "充值失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 用户扣费接口
     * @param param 包含 userId 和 amount 的请求参数
     * @return 操作结果
     */
    @PostMapping("/user/deduct")
    public Map<String, Object> deduct(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取参数
            Long userId = Long.parseLong(param.get("userId").toString());
            Double amount = Double.parseDouble(param.get("amount").toString());

            // 2. 参数校验
            if (userId <= 0 || amount <= 0) {
                result.put("code", 400);
                result.put("msg", "参数不合法");
                result.put("data", null);
                return result;
            }

            // 3. 调用 Service 执行扣费逻辑
            userService.deduct(userId, amount);

            // 4. 成功响应
            result.put("code", 200);
            result.put("msg", "扣费成功");
            result.put("data", null);
        } catch (RuntimeException e) {
            // 业务异常（如余额不足）
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            // 其他异常
            result.put("code", 500);
            result.put("msg", "扣费失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 更新用户VIP状态接口
     * @param param 包含 userId 和 period 的请求参数
     * @return 操作结果
     */
    @PostMapping("/user/updateVipStatus")
    public Map<String, Object> updateVipStatus(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取参数
            Long userId = Long.parseLong(param.get("userId").toString());
            String period = param.get("period").toString();

            // 2. 参数校验
            if (userId <= 0 || period == null || period.isEmpty()) {
                result.put("code", 400);
                result.put("msg", "参数不合法");
                result.put("data", null);
                return result;
            }

            // 3. 调用 Service 更新VIP状态
            userService.updateVipStatus(userId, period);

            // 4. 成功响应
            result.put("code", 200);
            result.put("msg", "更新VIP状态成功");
            result.put("data", null);
        } catch (RuntimeException e) {
            // 业务异常
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            // 其他异常
            result.put("code", 500);
            result.put("msg", "更新VIP状态失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 获取用户关注列表
     * @param userId 用户ID
     * @return 关注的用户列表
     */
    @GetMapping("/user/{userId}/following")
    public Result<List<User>> getFollowingList(@PathVariable Long userId) {
        try {
            List<User> followingList = userFollowService.getFollowingList(userId);
            return Result.success("获取关注列表成功", followingList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取关注列表异常：" + e.getMessage());
        }
    }

    /**
     * 获取用户粉丝列表
     * @param userId 用户ID
     * @return 粉丝列表
     */
    @GetMapping("/user/{userId}/followers")
    public Result<List<User>> getFollowersList(@PathVariable Long userId) {
        try {
            List<User> followersList = userFollowService.getFollowersList(userId);
            return Result.success("获取粉丝列表成功", followersList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取粉丝列表异常：" + e.getMessage());
        }
    }

}
