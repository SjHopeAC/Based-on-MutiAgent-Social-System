package com.example.helloworld.controller;

import com.example.helloworld.entity.PostComment;
import com.example.helloworld.entity.Result;
import com.example.helloworld.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class PostCommentController {

    @Autowired
    private PostCommentService commentService;

    // 1. 新增评论（支持回复：parentId=0为一级评论，>0为回复）
    @PostMapping("/create")
    public Result<Boolean> createComment(@RequestBody PostComment comment) {
        try {
            if (comment.getPostId() == null || comment.getCreatorId() == null || comment.getCommentContent() == null) {
                return Result.error("评论参数不完整");
            }
            // 默认parentId为0（一级评论）
            if (comment.getParentId() == null) {
                comment.setParentId(0L);
            }
            boolean success = commentService.createComment(comment);
            return success ? Result.success("评论成功", true) : Result.error("评论失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("评论异常：" + e.getMessage());
        }
    }

    // 2. 查询帖子的多级评论（B站风格）
    @GetMapping("/post/{postId}")
    public Result<List<PostComment>> getPostComments(@PathVariable Long postId) {
        try {
            List<PostComment> comments = commentService.getPostComments(postId);
            return Result.success("查询成功", comments);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询评论异常：" + e.getMessage());
        }
    }

    // 3. 评论点赞/取消点赞
    @PutMapping("/like/{commentId}")
    public Result<Boolean> updateCommentLike(@PathVariable Long commentId, @RequestParam Integer count) {
        try {
            commentService.updateCommentLikeCount(commentId, count);
            return Result.success("操作成功", true);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作异常：" + e.getMessage());
        }
    }

    // 4. 根据用户ID获取所有评论
    @GetMapping("/user/{userId}")
    public Result<List<PostComment>> getCommentsByUserId(@PathVariable Long userId) {
        try {
            List<PostComment> comments = commentService.getCommentsByUserId(userId);
            return Result.success("查询成功", comments);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询异常：" + e.getMessage());
        }
    }

    // 5. 申诉评论（将垃圾评论发到管理员待判决列表）
    @PostMapping("/{commentId}/appeal")
    public Result<Boolean> appealComment(@PathVariable Long commentId) {
        try {
            boolean success = commentService.appealComment(commentId);
            return success ? Result.success("申诉提交成功，请等待管理员审核", true) : Result.error("申诉提交失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("申诉异常：" + e.getMessage());
        }
    }

    // 6. 举报评论（将评论标记为待审核）
    @PostMapping("/{commentId}/report")
    public Result<Boolean> reportComment(@PathVariable Long commentId) {
        try {
            boolean success = commentService.reportComment(commentId);
            return success ? Result.success("举报成功，请等待管理员审核", true) : Result.error("举报失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("举报异常：" + e.getMessage());
        }
    }
}