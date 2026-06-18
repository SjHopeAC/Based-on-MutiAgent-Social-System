package com.example.helloworld.service;

import com.example.helloworld.entity.PrivateMessage;
import java.util.List;

/**
 * 私信Service接口
 */
public interface PrivateMessageService {
    /**
     * 发送私信
     */
    boolean sendMessage(PrivateMessage message);
    
    /**
     * 发送直接私信
     */
    boolean sendDirectMessage(Long senderId, Long receiverId, String content);
    
    /**
     * 分享话题
     */
    boolean shareTopic(Long senderId, Long receiverId, Long topicId, String content);
    
    /**
     * 分享帖子
     */
    boolean sharePost(Long senderId, Long receiverId, Long postId, String content);
    
    /**
     * 标记私信为已读
     */
    boolean markAsRead(Long id);
    
    /**
     * 批量标记私信为已读
     */
    boolean batchMarkAsRead(List<Long> ids);
    
    /**
     * 获取用户的私信列表
     */
    List<PrivateMessage> getMessagesByUserId(Long userId);
    
    /**
     * 获取用户与特定用户的私信对话
     */
    List<PrivateMessage> getConversation(Long userId, Long otherUserId);
    
    /**
     * 获取未读私信数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 删除私信
     */
    boolean deleteMessage(Long id);
}