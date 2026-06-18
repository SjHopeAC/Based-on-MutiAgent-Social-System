package com.example.helloworld.mapper;

import com.example.helloworld.entity.PrivateMessage;
import com.example.helloworld.entity.User;
import com.example.helloworld.entity.Topic;
import com.example.helloworld.entity.Post;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 私信Mapper接口
 */
@Mapper
public interface PrivateMessageMapper {
    /**
     * 根据用户ID获取用户信息
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(Long id);
    
    /**
     * 根据话题ID获取话题信息
     */
    @Select("SELECT * FROM topic WHERE topic_id = #{id}")
    Topic getTopicById(Long id);
    
    /**
     * 根据帖子ID获取帖子信息
     */
    @Select("SELECT * FROM post WHERE post_id = #{id}")
    Post getPostById(Long id);
    
    /**
     * 发送私信
     */
    @Insert("INSERT INTO private_message (sender_id, receiver_id, type, content, target_id, status, create_time, update_time) " +
            "VALUES (#{senderId}, #{receiverId}, #{type}, #{content}, #{targetId}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int sendMessage(PrivateMessage message);
    
    /**
     * 标记私信为已读
     */
    @Update("UPDATE private_message SET status = 1, update_time = NOW() WHERE id = #{id}")
    int markAsRead(Long id);
    
    /**
     * 批量标记私信为已读
     */
    @Update("UPDATE private_message SET status = 1, update_time = NOW() WHERE id IN (${ids})")
    int batchMarkAsRead(@Param("ids") String ids);
    
    /**
     * 获取用户的私信列表，包含发送者和接收者信息
     */
    @Select("SELECT pm.*, " +
            "s.id as sender_id, s.nickname as sender_nickname, s.avatar as sender_avatar, " +
            "r.id as receiver_id, r.nickname as receiver_nickname, r.avatar as receiver_avatar " +
            "FROM private_message pm " +
            "LEFT JOIN user s ON pm.sender_id = s.id " +
            "LEFT JOIN user r ON pm.receiver_id = r.id " +
            "WHERE pm.receiver_id = #{userId} OR pm.sender_id = #{userId} " +
            "ORDER BY pm.create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "receiverId", column = "receiver_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "content", column = "content"),
        @Result(property = "targetId", column = "target_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "sender", column = "sender_id", javaType = User.class,
            one = @One(select = "getUserById")),
        @Result(property = "receiver", column = "receiver_id", javaType = User.class,
            one = @One(select = "getUserById")),
        @Result(property = "topic", column = "target_id", javaType = Topic.class,
            one = @One(select = "getTopicById")),
        @Result(property = "post", column = "target_id", javaType = Post.class,
            one = @One(select = "getPostById"))
    })
    List<PrivateMessage> getMessagesByUserId(Long userId);
    
    /**
     * 获取用户与特定用户的私信对话，包含发送者和接收者信息
     */
    @Select("SELECT pm.*, " +
            "s.id as sender_id, s.nickname as sender_nickname, s.avatar as sender_avatar, " +
            "r.id as receiver_id, r.nickname as receiver_nickname, r.avatar as receiver_avatar " +
            "FROM private_message pm " +
            "LEFT JOIN user s ON pm.sender_id = s.id " +
            "LEFT JOIN user r ON pm.receiver_id = r.id " +
            "WHERE (pm.sender_id = #{userId} AND pm.receiver_id = #{otherUserId}) " +
            "OR (pm.sender_id = #{otherUserId} AND pm.receiver_id = #{userId}) " +
            "ORDER BY pm.create_time ASC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "receiverId", column = "receiver_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "content", column = "content"),
        @Result(property = "targetId", column = "target_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "sender", column = "sender_id", javaType = User.class,
            one = @One(select = "getUserById")),
        @Result(property = "receiver", column = "receiver_id", javaType = User.class,
            one = @One(select = "getUserById")),
        @Result(property = "topic", column = "target_id", javaType = Topic.class,
            one = @One(select = "getTopicById")),
        @Result(property = "post", column = "target_id", javaType = Post.class,
            one = @One(select = "getPostById"))
    })
    List<PrivateMessage> getConversation(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);
    
    /**
     * 获取未读私信数量
     */
    @Select("SELECT COUNT(*) FROM private_message WHERE receiver_id = #{userId} AND status = 0")
    int getUnreadCount(Long userId);
    
    /**
     * 删除私信
     */
    @Delete("DELETE FROM private_message WHERE id = #{id}")
    int deleteMessage(Long id);
    
    /**
     * 增加话题分享次数
     */
    @Update("UPDATE topic SET share_count = share_count + 1 WHERE topic_id = #{topicId}")
    int incrementTopicShareCount(Long topicId);
    
    /**
     * 增加帖子分享次数
     */
    @Update("UPDATE post SET share_count = share_count + 1 WHERE post_id = #{postId}")
    int incrementPostShareCount(Long postId);
}