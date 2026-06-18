package com.example.helloworld.service.impl;

import com.example.helloworld.entity.PrivateMessage;
import com.example.helloworld.mapper.PrivateMessageMapper;
import com.example.helloworld.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 私信Service实现类
 */
@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {
    
    @Autowired
    private PrivateMessageMapper messageMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendMessage(PrivateMessage message) {
        message.setStatus(0); // 初始状态为未读
        return messageMapper.sendMessage(message) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendDirectMessage(Long senderId, Long receiverId, String content) {
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setType(1); // 直接私信
        message.setContent(content);
        message.setTargetId(null);
        message.setStatus(0);
        return messageMapper.sendMessage(message) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shareTopic(Long senderId, Long receiverId, Long topicId, String content) {
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setType(2); // 分享话题
        message.setContent(content);
        message.setTargetId(topicId);
        message.setStatus(0);
        
        // 发送私信
        boolean success = messageMapper.sendMessage(message) > 0;
        
        // 增加话题分享次数（如果数据库支持）
        try {
            messageMapper.incrementTopicShareCount(topicId);
        } catch (Exception e) {
            // 如果数据库表没有share_count字段，忽略此操作
            System.out.println("话题分享次数更新失败（可能数据库表结构不支持）：" + e.getMessage());
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sharePost(Long senderId, Long receiverId, Long postId, String content) {
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setType(3); // 分享帖子
        message.setContent(content);
        message.setTargetId(postId);
        message.setStatus(0);
        
        // 发送私信
        boolean success = messageMapper.sendMessage(message) > 0;
        
        // 增加帖子分享次数（如果数据库支持）
        try {
            messageMapper.incrementPostShareCount(postId);
        } catch (Exception e) {
            // 如果数据库表没有share_count字段，忽略此操作
            System.out.println("帖子分享次数更新失败（可能数据库表结构不支持）：" + e.getMessage());
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long id) {
        return messageMapper.markAsRead(id) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkAsRead(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String idsStr = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        return messageMapper.batchMarkAsRead(idsStr) > 0;
    }
    
    @Override
    public List<PrivateMessage> getMessagesByUserId(Long userId) {
        return messageMapper.getMessagesByUserId(userId);
    }
    
    @Override
    public List<PrivateMessage> getConversation(Long userId, Long otherUserId) {
        return messageMapper.getConversation(userId, otherUserId);
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        return messageMapper.getUnreadCount(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMessage(Long id) {
        return messageMapper.deleteMessage(id) > 0;
    }
}