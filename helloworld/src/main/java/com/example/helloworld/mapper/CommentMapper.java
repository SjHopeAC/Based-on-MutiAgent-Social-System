package com.example.helloworld.mapper;

import com.example.helloworld.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
    
    /**
     * 获取所有帖子评论（包含用户信息）
     */
    @Select("SELECT pc.comment_id as id, pc.creator_id as userId, pc.post_id as targetId, 1 as targetType, " +
            "pc.comment_content as content, pc.like_count as likeCount, 0 as replyCount, pc.status, pc.comment_type as commentType, pc.create_time as createTime, " +
            "pc.confidence, pc.reasoning, pc.result, pc.source, " +
            "u.username, u.nickname, u.avatar " +
            "FROM post_comment pc " +
            "LEFT JOIN user u ON pc.creator_id = u.id " +
            "ORDER BY pc.create_time DESC")
    List<Map<String, Object>> selectAllComments();
    
    /**
     * 根据评论ID获取回复列表（包含用户信息）
     */
    @Select("SELECT pc.comment_id as id, pc.creator_id as userId, pc.post_id as targetId, " +
            "pc.comment_content as content, pc.create_time as createTime, " +
            "u.username, u.nickname, u.avatar " +
            "FROM post_comment pc " +
            "LEFT JOIN user u ON pc.creator_id = u.id " +
            "WHERE pc.parent_id = #{commentId} " +
            "ORDER BY pc.create_time ASC")
    List<Map<String, Object>> selectRepliesByCommentId(Long commentId);
    
    /**
     * 更新评论状态
     */
    @Update("UPDATE post_comment SET status = #{status}, update_time = NOW() WHERE comment_id = #{commentId}")
    int updateCommentStatus(@Param("commentId") Long commentId, @Param("status") Integer status);
    
    /**
     * 更新评论类型和状态
     */
    @Update("UPDATE post_comment SET comment_type = #{commentType}, status = #{status}, update_time = NOW() WHERE comment_id = #{commentId}")
    int updateCommentTypeAndStatus(@Param("commentId") Long commentId, @Param("commentType") Integer commentType, @Param("status") Integer status);
    
    /**
     * 统计用户评论总数
     */
    @Select("SELECT COUNT(*) FROM post_comment WHERE creator_id = #{userId}")
    int countUserComments(@Param("userId") Long userId);
    
    /**
     * 统计用户垃圾评论数
     */
    @Select("SELECT COUNT(*) FROM post_comment WHERE creator_id = #{userId} AND comment_type = -2")
    int countUserSpamComments(@Param("userId") Long userId);

    /**
     * 根据帖子ID删除评论
     */
    @Delete("DELETE FROM post_comment WHERE post_id = #{postId}")
    int deleteCommentsByPostId(@Param("postId") Long postId);
}
