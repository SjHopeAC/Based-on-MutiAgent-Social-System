package com.example.helloworld.mapper;

import com.example.helloworld.entity.PostLike;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PostLikeMapper {
    
    @Insert("INSERT INTO post_like (post_id, user_id, create_time) VALUES (#{postId}, #{userId}, NOW())")
    int insertPostLike(PostLike postLike);
    
    @Delete("DELETE FROM post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    int deletePostLike(@Param("postId") Long postId, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    int countPostLike(@Param("postId") Long postId, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM post_like WHERE post_id = #{postId}")
    int countPostLikesByPostId(@Param("postId") Long postId);
}