package com.example.helloworld.service.impl;

import com.example.helloworld.entity.PostComment;
import com.example.helloworld.entity.User;
import com.example.helloworld.entity.UserNotice;
import com.example.helloworld.entity.Post;
import com.example.helloworld.mapper.PostCommentMapper;
import com.example.helloworld.service.PostCommentService;
import com.example.helloworld.service.PostService;
import com.example.helloworld.service.UserService;
import com.example.helloworld.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostCommentServiceImpl implements PostCommentService {

    @Autowired
    private PostCommentMapper commentMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private NoticeService noticeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createComment(PostComment comment) {
        // 1. 新增评论（确保parentId为0时保存为NULL）
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            comment.setParentId(null);
        }
        int result = commentMapper.insertComment(comment);
        // 2. 更新帖子的评论数（无论是否是一级评论都更新）
        postService.incrementCommentCount(comment.getPostId());
        
        // 3. 更新用户评论数和垃圾评论数
        if (result > 0 && comment.getCreatorId() != null) {
            // 增加用户评论数
            userService.incrementCommentCount(comment.getCreatorId());
            
            // 如果是垃圾评论，增加垃圾评论数
            if (comment.getCommentType() == -2) {
                userService.incrementSpamCommentCount(comment.getCreatorId());
            }
            
            // 检查是否为劣迹用户（垃圾评论数超过总评论数的一半）
            User user = userService.getUserById(comment.getCreatorId());
            if (user != null) {
                int commentCount = user.getCommentCount() != null ? user.getCommentCount() : 0;
                int spamCommentCount = user.getSpamCommentCount() != null ? user.getSpamCommentCount() : 0;
                
                // 如果总评论数大于0，且垃圾评论数超过总评论数的一半
                if (commentCount > 0 && spamCommentCount > commentCount / 2) {
                    userService.updateSpamUserStatus(comment.getCreatorId(), true);
                }
            }
            
            // 4. 发送评论成功通知
            // 给评论者发送通知（根据评论类型）
            UserNotice notice = new UserNotice();
            notice.setUserId(comment.getCreatorId());
            notice.setFromUserId(0L); // 系统通知
            notice.setNoticeType(UserNotice.NOTICE_TYPE_SYSTEM);
            notice.setTargetType(UserNotice.TARGET_TYPE_COMMENT);
            notice.setTargetId(comment.getCommentId());
            // 根据评论类型设置通知内容
            if (comment.getCommentType() == -2) {
                notice.setContent("您的评论经过判定认为是垃圾评论，已被封禁");
            } else {
                notice.setContent("您的评论已成功发布");
            }
            notice.setIsRead(0); // 未读
            noticeService.addNotice(notice);
            
            // 给帖子作者发送通知
            Post post = postService.getPostById(comment.getPostId(), null);
            if (post != null && post.getUserId() != null && !post.getUserId().equals(comment.getCreatorId())) {
                User commentUser = userService.getUserById(comment.getCreatorId());
                String commentUserName = commentUser != null ? commentUser.getNickname() : "用户";
                
                UserNotice postAuthorNotice = new UserNotice();
                postAuthorNotice.setUserId(post.getUserId());
                postAuthorNotice.setFromUserId(comment.getCreatorId());
                postAuthorNotice.setNoticeType(3); // 3-评论通知
                postAuthorNotice.setTargetType(1); // 1-帖子
                postAuthorNotice.setTargetId(comment.getPostId());
                postAuthorNotice.setContent(commentUserName + " 在你的帖子下评论了");
                postAuthorNotice.setIsRead(0); // 未读
                noticeService.addNotice(postAuthorNotice);
            }
        }
        
        return result > 0;
    }

    @Override
    public List<PostComment> getPostComments(Long postId) {
        // 1. 查询所有一级评论（parent_id=0）
        List<PostComment> firstLevelComments = commentMapper.selectFirstLevelComments(postId);

        // 2. 递归组装每个一级评论的子评论（B站风格：限制最大层级为3）
        firstLevelComments.forEach(comment -> {
            // 补充评论用户信息
            User creator = userService.getUserById(comment.getCreatorId());
            comment.setCreator(creator);
            // 组装子评论，最大层级为3
            assembleChildComments(comment, 1);
        });

        return firstLevelComments;
    }

    // 递归组装子评论（核心：实现多级回复，限制最大层级）
    private void assembleChildComments(PostComment parentComment, int currentLevel) {
        // 超过最大层级（3层），不再递归
        if (currentLevel > 3) {
            return;
        }

        // 查询当前评论的子评论（包含回复对象信息）
        List<PostComment> childComments = commentMapper.selectChildCommentsWithReplyUser(parentComment.getCommentId());
        if (childComments.isEmpty()) {
            return;
        }

        // 补充子评论的用户信息和回复对象信息，并递归组装子评论的子评论
        childComments.forEach(child -> {
            User creator = userService.getUserById(child.getCreatorId());
            child.setCreator(creator);

            // 如果是回复评论，查询被回复的用户信息
            if (child.getReplyToUserId() != null) {
                User replyToUser = userService.getUserById(child.getReplyToUserId());
                child.setReplyToUser(replyToUser);
            }

            // 递归：子评论可能还有回复，层级+1
            assembleChildComments(child, currentLevel + 1);
        });

        // 设置子评论列表
        parentComment.setChildren(childComments);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommentLikeCount(Long commentId, Integer count) {
        commentMapper.updateLikeCount(commentId, count);
    }

    @Override
    public List<PostComment> getCommentsByUserId(Long userId) {
        try {
            List<PostComment> comments = commentMapper.selectCommentsByUserId(userId);
            // 补充每个评论的用户信息
            comments.forEach(comment -> {
                User creator = userService.getUserById(comment.getCreatorId());
                comment.setCreator(creator);
            });
            return comments;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean appealComment(Long commentId) {
        try {
            // 将垃圾评论（commentType = -2）改为待评评论（commentType = 0）
            // 并设置appealed字段为true
            int result = commentMapper.appealComment(commentId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reportComment(Long commentId) {
        try {
            // 将评论改为待评评论（commentType = 0）
            // 并设置appealed字段为true
            int result = commentMapper.reportComment(commentId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}