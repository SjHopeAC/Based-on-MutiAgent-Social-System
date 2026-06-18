package com.example.helloworld.controller;

import com.example.helloworld.entity.PrivateMessage;
import com.example.helloworld.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 私信Controller
 */
@RestController
@RequestMapping("/message")
public class PrivateMessageController {
    
    @Autowired
    private PrivateMessageService messageService;
    
    /**
     * 发送私信
     */
    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody PrivateMessage message) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.sendMessage(message);
            if (success) {
                result.put("code", 200);
                result.put("msg", "发送成功");
                result.put("data", message);
            } else {
                result.put("code", 500);
                result.put("msg", "发送失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "发送失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 发送直接私信
     */
    @PostMapping("/direct")
    public Map<String, Object> sendDirectMessage(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long senderId = Long.parseLong(params.get("senderId").toString());
            Long receiverId = Long.parseLong(params.get("receiverId").toString());
            String content = params.get("content").toString();
            
            boolean success = messageService.sendDirectMessage(senderId, receiverId, content);
            if (success) {
                result.put("code", 200);
                result.put("msg", "发送成功");
                result.put("data", null);
            } else {
                result.put("code", 500);
                result.put("msg", "发送失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "发送失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 分享话题
     */
    @PostMapping("/share/topic")
    public Map<String, Object> shareTopic(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long senderId = Long.parseLong(params.get("senderId").toString());
            Long receiverId = Long.parseLong(params.get("receiverId").toString());
            Long topicId = Long.parseLong(params.get("topicId").toString());
            String content = params.getOrDefault("content", "").toString();
            
            boolean success = messageService.shareTopic(senderId, receiverId, topicId, content);
            if (success) {
                result.put("code", 200);
                result.put("msg", "分享成功");
                result.put("data", null);
            } else {
                result.put("code", 500);
                result.put("msg", "分享失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "分享失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 分享帖子
     */
    @PostMapping("/share/post")
    public Map<String, Object> sharePost(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long senderId = Long.parseLong(params.get("senderId").toString());
            Long receiverId = Long.parseLong(params.get("receiverId").toString());
            Long postId = Long.parseLong(params.get("postId").toString());
            String content = params.getOrDefault("content", "").toString();
            
            boolean success = messageService.sharePost(senderId, receiverId, postId, content);
            if (success) {
                result.put("code", 200);
                result.put("msg", "分享成功");
                result.put("data", null);
            } else {
                result.put("code", 500);
                result.put("msg", "分享失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "分享失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 标记私信为已读
     */
    @PutMapping("/read/{id}")
    public Map<String, Object> markAsRead(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.markAsRead(id);
            if (success) {
                result.put("code", 200);
                result.put("msg", "标记成功");
                result.put("data", null);
            } else {
                result.put("code", 500);
                result.put("msg", "标记失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "标记失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 批量标记私信为已读
     */
    @PutMapping("/read/batch")
    public Map<String, Object> batchMarkAsRead(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.batchMarkAsRead(ids);
            if (success) {
                result.put("code", 200);
                result.put("msg", "标记成功");
                result.put("data", null);
            } else {
                result.put("code", 500);
                result.put("msg", "标记失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "标记失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 获取用户的私信列表
     */
    @GetMapping("/list/{userId}")
    public Map<String, Object> getMessagesByUserId(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PrivateMessage> messages = messageService.getMessagesByUserId(userId);
            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", messages);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "获取失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 获取用户与特定用户的私信对话
     */
    @GetMapping("/conversation/{userId}/{otherUserId}")
    public Map<String, Object> getConversation(@PathVariable Long userId, @PathVariable Long otherUserId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PrivateMessage> messages = messageService.getConversation(userId, otherUserId);
            // 自动标记为已读
            List<Long> unreadIds = messages.stream()
                .filter(msg -> msg.getReceiverId().equals(userId) && msg.getStatus() == 0)
                .map(PrivateMessage::getId)
                .collect(java.util.stream.Collectors.toList());
            if (!unreadIds.isEmpty()) {
                messageService.batchMarkAsRead(unreadIds);
            }
            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", messages);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "获取失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 获取未读私信数量
     */
    @GetMapping("/unread/count/{userId}")
    public Map<String, Object> getUnreadCount(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = messageService.getUnreadCount(userId);
            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", count);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "获取失败：" + e.getMessage());
            result.put("data", 0);
        }
        return result;
    }
    
    /**
     * 删除私信
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteMessage(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.deleteMessage(id);
            if (success) {
                result.put("code", 200);
                result.put("msg", "删除成功");
                result.put("data", null);
            } else {
                result.put("code", 500);
                result.put("msg", "删除失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "删除失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
}