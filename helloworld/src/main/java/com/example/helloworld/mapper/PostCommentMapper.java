package com.example.helloworld.mapper;

import com.example.helloworld.entity.PostComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostCommentMapper {
    // 1. 新增评论
    @Insert("INSERT INTO post_comment (post_id, parent_id, creator_id, comment_content, comment_type, status, confidence, reasoning, result, source) " +
            "VALUES (#{postId}, #{parentId}, #{creatorId}, #{commentContent}, #{commentType}, #{status}, #{confidence}, #{reasoning}, #{result}, #{source})")
    int insertComment(PostComment comment);

    // 2. 查询帖子的所有一级评论（parent_id=0或NULL）
    @Select("SELECT comment_id as commentId, post_id as postId, parent_id as parentId, " +
            "creator_id as creatorId, comment_content as commentContent, status, like_count as likeCount, " +
            "comment_type as commentType, confidence, reasoning, result, source, " +
            "create_time as createTime, update_time as updateTime " +
            "FROM post_comment WHERE post_id = #{postId} AND (parent_id = 0 OR parent_id IS NULL) AND status = 1 " +
            "ORDER BY create_time DESC")
    List<PostComment> selectFirstLevelComments(Long postId);

    // 3. 根据父评论ID查询子评论
    @Select("SELECT comment_id as commentId, post_id as postId, parent_id as parentId, " +
            "creator_id as creatorId, comment_content as commentContent, status, like_count as likeCount, " +
            "comment_type as commentType, confidence, reasoning, result, source, " +
            "create_time as createTime, update_time as updateTime " +
            "FROM post_comment WHERE parent_id = #{parentId} AND status = 1 " +
            "ORDER BY create_time ASC")
    List<PostComment> selectChildComments(Long parentId);

    // 4. 更新评论点赞数
    @Update("UPDATE post_comment SET like_count = like_count + #{count} WHERE comment_id = #{commentId}")
    int updateLikeCount(@Param("commentId") Long commentId, @Param("count") Integer count);

    // 5. 根据用户ID查询所有评论
    @Select("SELECT comment_id as commentId, post_id as postId, parent_id as parentId, " +
            "creator_id as creatorId, comment_content as commentContent, status, like_count as likeCount, " +
            "comment_type as commentType, appealed, confidence, reasoning, result, source, " +
            "create_time as createTime, update_time as updateTime " +
            "FROM post_comment WHERE creator_id = #{userId} " +
            "ORDER BY create_time DESC")
    List<PostComment> selectCommentsByUserId(Long userId);

    // 6. 根据评论ID查询评论详情（包含回复对象信息）
    @Select("SELECT c1.comment_id as commentId, c1.post_id as postId, c1.parent_id as parentId, " +
            "c1.creator_id as creatorId, c1.comment_content as commentContent, c1.status, c1.like_count as likeCount, " +
            "c1.comment_type as commentType, c1.confidence, c1.reasoning, c1.result, c1.source, " +
            "c1.create_time as createTime, c1.update_time as updateTime, " +
            "c2.creator_id as replyToUserId " +
            "FROM post_comment c1 " +
            "LEFT JOIN post_comment c2 ON c1.parent_id = c2.comment_id " +
            "WHERE c1.comment_id = #{commentId} AND c1.status = 1")
    PostComment selectCommentWithReplyUser(@Param("commentId") Long commentId);

    // 7. 根据父评论ID查询子评论（包含回复对象信息）
    @Select("SELECT c1.comment_id as commentId, c1.post_id as postId, c1.parent_id as parentId, " +
            "c1.creator_id as creatorId, c1.comment_content as commentContent, c1.status, c1.like_count as likeCount, " +
            "c1.comment_type as commentType, c1.confidence, c1.reasoning, c1.result, c1.source, " +
            "c1.create_time as createTime, c1.update_time as updateTime, " +
            "c2.creator_id as replyToUserId " +
            "FROM post_comment c1 " +
            "LEFT JOIN post_comment c2 ON c1.parent_id = c2.comment_id " +
            "WHERE c1.parent_id = #{parentId} AND c1.status = 1 " +
            "ORDER BY c1.create_time ASC")
    List<PostComment> selectChildCommentsWithReplyUser(@Param("parentId") Long parentId);

    // 8. 申诉评论：将垃圾评论改为待评评论，并设置appealed为true
    @Update("UPDATE post_comment SET comment_type = 0, appealed = 1 WHERE comment_id = #{commentId} AND comment_type = -2")
    int appealComment(@Param("commentId") Long commentId);
    
    // 9. 举报评论：将评论改为待评评论，并设置appealed为true
    @Update("UPDATE post_comment SET comment_type = 0, appealed = 1 WHERE comment_id = #{commentId}")
    int reportComment(@Param("commentId") Long commentId);
}