// PostCommentService 接口
package com.example.helloworld.service;

import com.example.helloworld.entity.PostComment;
import java.util.List;

public interface PostCommentService {
    // 新增评论（支持回复）
    boolean createComment(PostComment comment);
    // 查询帖子的多级评论（B站风格：一级评论+嵌套子评论）
    List<PostComment> getPostComments(Long postId);
    // 更新评论点赞数
    void updateCommentLikeCount(Long commentId, Integer count);
    // 根据用户ID获取所有评论
    List<PostComment> getCommentsByUserId(Long userId);
    // 申诉评论（将垃圾评论发到管理员待判决列表）
    boolean appealComment(Long commentId);
    // 举报评论（将评论标记为待审核）
    boolean reportComment(Long commentId);
}
