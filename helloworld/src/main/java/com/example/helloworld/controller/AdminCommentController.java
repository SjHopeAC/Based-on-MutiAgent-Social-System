package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.UserNotice;
import com.example.helloworld.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员评论管理Controller
 */
@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {
    
    @Autowired
    private com.example.helloworld.mapper.CommentMapper commentMapper;
    
    @Autowired
    private NoticeService noticeService;
    
    /**
     * 获取评论列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getCommentList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer commentType) {
        try {
            int offset = (page - 1) * size;
            
            List<Map<String, Object>> comments;
            int total;
            
            comments = commentMapper.selectAllComments();
            
            // 过滤keyword
            if (keyword != null && !keyword.isEmpty()) {
                comments = comments.stream().filter(c -> 
                    c.get("content") != null && c.get("content").toString().contains(keyword)
                ).collect(Collectors.toList());
            }
            
            // 过滤状态
            if (status != null) {
                comments = comments.stream().filter(c -> c.get("status").equals(status)).collect(Collectors.toList());
            }
            
            // 过滤评论类型
            if (commentType != null) {
                comments = comments.stream().filter(c -> c.get("commentType").equals(commentType)).collect(Collectors.toList());
            }
            
            total = comments.size();
            
            // 分页处理
            int fromIndex = Math.min(offset, comments.size());
            int toIndex = Math.min(offset + size, comments.size());
            List<Map<String, Object>> pageList = comments.subList(fromIndex, toIndex);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageList);
            result.put("total", total);
            
            return Result.success("获取评论列表成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取评论列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取评论的回复列表
     */
    @GetMapping("/{commentId}/replies")
    public Result<List<Map<String, Object>>> getReplyList(@PathVariable Long commentId) {
        try {
            List<Map<String, Object>> replies = commentMapper.selectRepliesByCommentId(commentId);
            return Result.success("获取回复列表成功", replies);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取回复列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<String> deleteComment(@PathVariable Long commentId) {
        try {
            int result = commentMapper.updateCommentStatus(commentId, 0);
            if (result > 0) {
                return Result.success("删除成功", "删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 恢复评论
     */
    @PutMapping("/{commentId}/restore")
    public Result<String> restoreComment(@PathVariable Long commentId) {
        try {
            int result = commentMapper.updateCommentStatus(commentId, 1);
            if (result > 0) {
                return Result.success("恢复成功", "恢复成功");
            } else {
                return Result.error("恢复失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("恢复失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新评论类型和状态
     */
    @PutMapping("/{commentId}")
    public Result<String> updateComment(@PathVariable Long commentId, @RequestBody Map<String, Object> updateData) {
        try {
            Integer commentType = null;
            Integer status = null;
            
            // 处理commentType参数
            Object commentTypeObj = updateData.get("commentType");
            if (commentTypeObj != null) {
                if (commentTypeObj instanceof String) {
                    commentType = Integer.parseInt((String) commentTypeObj);
                } else if (commentTypeObj instanceof Integer) {
                    commentType = (Integer) commentTypeObj;
                }
            }
            
            // 处理status参数
            Object statusObj = updateData.get("status");
            if (statusObj != null) {
                if (statusObj instanceof String) {
                    status = Integer.parseInt((String) statusObj);
                } else if (statusObj instanceof Integer) {
                    status = (Integer) statusObj;
                }
            }
            
            int result = commentMapper.updateCommentTypeAndStatus(commentId, commentType, status);
            
            if (result > 0) {
                // 获取评论的用户ID
                List<Map<String, Object>> comments = commentMapper.selectAllComments();
                Long userId = null;
                for (Map<String, Object> comment : comments) {
                    if (comment.get("id").equals(commentId)) {
                        userId = Long.valueOf(comment.get("userId").toString());
                        break;
                    }
                }
                
                // 发送通知给用户
                if (userId != null) {
                    UserNotice notice = new UserNotice();
                    notice.setUserId(userId);
                    notice.setFromUserId(0L); // 系统通知
                    notice.setNoticeType(UserNotice.NOTICE_TYPE_SYSTEM);
                    notice.setTargetType(UserNotice.TARGET_TYPE_COMMENT);
                    notice.setTargetId(commentId);
                    
                    // 根据修改内容生成通知内容
                    String content = "您的评论状态已更新";
                    if (commentType != null) {
                        switch (commentType) {
                            case 1:
                                content = "您的评论被评为正向评论";
                                break;
                            case 0:
                                content = "您的评论被评为待评评论";
                                break;
                            case -1:
                                content = "您的评论被评为负向评论";
                                break;
                            case -2:
                                content = "您的评论被评为垃圾评论";
                                break;
                        }
                    }
                    
                    if (status != null) {
                        if (status == 1) {
                            content += "，状态已恢复";
                        } else if (status == 0) {
                            content += "，状态已封禁";
                        }
                    }
                    
                    notice.setContent(content);
                    notice.setIsRead(0); // 未读
                    noticeService.addNotice(notice);
                    
                    // 如果评论类型是垃圾评论，检查用户是否为垃圾用户
                    if (commentType != null && commentType == -2) {
                        checkAndMarkSpamUser(userId);
                    }
                }
                
                return Result.success("更新评论成功", "更新评论成功");
            } else {
                return Result.error("更新评论失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新评论失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查并标记垃圾用户
     */
    private void checkAndMarkSpamUser(Long userId) {
        try {
            int totalComments = commentMapper.countUserComments(userId);
            int spamComments = commentMapper.countUserSpamComments(userId);
            
            // 如果垃圾评论数占评论总数的1/2以上，标记为垃圾用户
            if (totalComments > 0 && spamComments >= totalComments / 2) {
                // 这里可以添加标记用户为垃圾用户的逻辑
                // 例如更新用户表中的状态字段
                System.out.println("用户 " + userId + " 被标记为垃圾用户，垃圾评论数: " + spamComments + "，总评论数: " + totalComments);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
