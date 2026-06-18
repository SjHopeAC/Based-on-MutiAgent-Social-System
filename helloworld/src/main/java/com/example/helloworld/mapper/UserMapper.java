package com.example.helloworld.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.helloworld.entity.User;
import com.example.helloworld.entity.UserFollow;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectUserByUsername(@Param("username") String username);


    //获取所有用户
    @Select("select * from user")
    public List<User> getAllUser();

    @Select("select * from user where id=#{id}")
    public User getUserById(@Param("id") Long id);

    // 根据用户名+手机号查询用户
    @Select("SELECT * FROM user WHERE username = #{username} AND phone = #{phone}")
    User selectUserByUsernameAndPhone(@Param("username") String username, @Param("phone") String phone);

    // 根据用户名+邮箱查询用户
    @Select("SELECT * FROM user WHERE username = #{username} AND email = #{email}")
    User selectUserByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    // 更新密码
    @Update("UPDATE user SET password = #{newPassword}, update_time = NOW() WHERE id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    // 检查用户名是否已存在
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    // 检查邮箱是否已存在
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    // 插入新用户（注册核心）
    @Insert("INSERT INTO user (username, email, password, nickname,  create_time, update_time) " +
            "VALUES (#{username}, #{email}, #{password}, #{username}, NOW(), NOW())")
    int insertUser(User user);

    //注册时校验用户名是否已存在（避免唯一索引冲突）
    @Select("select count(1) from user where username = #{username}")
    int checkUsernameExists(@Param("username") String username);


    @Select("select * from user")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "nickname", property = "nickname"),
            @Result(
                    column = "id",
                    property = "Topics",
                    javaType = List.class,
                    many = @Many(select = "com.example.helloworld.mapper.TopicMapper.getTopicByUserId")
            )
    })
    public List<User> getAllUsersAndTopics();

    // 查询活跃用户（按最后登录时间排序）
    @Select("SELECT * FROM user WHERE last_login_time IS NOT NULL ORDER BY last_login_time DESC LIMIT 3")
    List<User> selectActiveUsers();

    // 查询用户总数
    @Select("SELECT COUNT(*) FROM user")
    int selectCount();

    // 新增：更新最后登录时间
    @Update("UPDATE user SET last_login_time = #{lastLoginTime} WHERE id = #{userId}")
    int updateLastLoginTime(@Param("userId") Long userId, @Param("lastLoginTime") Date lastLoginTime);


    /**
     * 关注数+1
     */
    @Update("UPDATE user SET follow_count = follow_count + 1 WHERE id = #{userId}")
    int incrFollowCount(@Param("userId") Long userId);

    /**
     * 关注数-1
     */
    @Update("UPDATE user SET follow_count = follow_count - 1 WHERE id = #{userId} AND follow_count > 0")
    int decrFollowCount(@Param("userId") Long userId);

    /**
     * 粉丝数+1
     */
    @Update("UPDATE user SET fans_count = fans_count + 1 WHERE id = #{userId}")
    int incrFansCount(@Param("userId") Long userId);

    /**
     * 粉丝数-1
     */
    @Update("UPDATE user SET fans_count = fans_count - 1 WHERE id = #{userId} AND fans_count > 0")
    int decrFansCount(@Param("userId") Long userId);

    /**
     * 更新用户头像
     * @param username 用户名
     * @param avatar 新头像路径
     * @return 受影响行数
     */
    @Update("UPDATE user SET avatar = #{avatar} WHERE username = #{username}")
    int updateUserAvatar(@Param("username") String username, @Param("avatar") String avatar);

    @Select("SELECT * FROM user WHERE id = #{userId}")
    User selectById(@Param("userId") Long userId);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 受影响行数
     */
    @Update("UPDATE user SET nickname = #{nickname}, bio = #{bio}, email = #{email}, phone = #{phone}, gender = #{gender}, update_time = NOW() WHERE id = #{id}")
    int updateUserInfo(User user);

    /**
     * 更新用户发帖数（+1）
     */
    @Update("UPDATE user SET post_count = post_count + 1 WHERE id = #{userId}")
    int incrementPostCount(@Param("userId") Long userId);

    /**
     * 更新用户评论数（+1）
     */
    @Update("UPDATE user SET comment_count = comment_count + 1 WHERE id = #{userId}")
    int incrementCommentCount(@Param("userId") Long userId);

    /**
     * 更新用户垃圾评论数（+1）
     */
    @Update("UPDATE user SET spam_comment_count = spam_comment_count + 1 WHERE id = #{userId}")
    int incrementSpamCommentCount(@Param("userId") Long userId);

    /**
     * 更新用户劣迹状态
     */
    @Update("UPDATE user SET is_spam_user = #{isSpamUser} WHERE id = #{userId}")
    int updateSpamUserStatus(@Param("userId") Long userId, @Param("isSpamUser") Boolean isSpamUser);

    /**
     * 更新账户余额
     * @param user 用户信息
     * @return 受影响行数
     */
    @Update("UPDATE user SET balance = #{balance}, update_time = NOW() WHERE id = #{id}")
    int updateUserBalance(User user);

    /**
     * 搜索用户（按用户名或昵称模糊搜索）
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    @Select("SELECT id, username, nickname, avatar, bio, follow_count, fans_count, post_count, like_count, is_vip, status " +
            "FROM user " +
            "WHERE (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY last_login_time DESC " +
            "LIMIT 20")
    List<User> searchUsers(@Param("keyword") String keyword);

    /**
     * 更新用户VIP状态和过期时间
     * @param userId 用户ID
     * @param vipExpireTime 过期时间
     * @return 受影响行数
     */
    @Update("UPDATE user SET is_vip = 1, vip_expire_time = #{vipExpireTime}, update_time = NOW() WHERE id = #{userId}")
    int updateVipStatus(@Param("userId") Long userId, @Param("vipExpireTime") Date vipExpireTime);

    /**
     * 获取帖子总数
     */
    @Select("SELECT COUNT(*) FROM post WHERE status = 1")
    int selectPostCount();

    /**
     * 获取话题总数
     */
    @Select("SELECT COUNT(*) FROM topic WHERE status = 1")
    int selectTopicCount();

    /**
     * 获取评论总数
     */
    @Select("SELECT COUNT(*) FROM post_comment WHERE status = 1")
    int selectCommentCount();

    /**
     * 获取最新用户
     */
    @Select("SELECT id, username, nickname, avatar, email, phone, follow_count, fans_count, post_count, is_vip, status, create_time " +
            "FROM user " +
            "ORDER BY create_time DESC " +
            "LIMIT #{limit}")
    List<User> selectRecentUsers(@Param("limit") int limit);

    /**
     * 获取热门话题
     */
    @Select("SELECT topic_id as topicId, topic_title as topicTitle, view_count as viewCount, like_count as likeCount, participant_count as participantCount " +
            "FROM topic " +
            "WHERE status = 1 " +
            "ORDER BY (view_count + like_count * 2 + participant_count * 3) DESC " +
            "LIMIT #{limit}")
    List<java.util.Map<String, Object>> selectHotTopics(@Param("limit") int limit);

    /**
     * 更新用户状态
     */
    @Update("UPDATE user SET status = #{status}, update_time = NOW() WHERE id = #{userId}")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") int status);
}