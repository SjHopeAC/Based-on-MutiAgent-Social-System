package com.example.helloworld.mapper;

import com.example.helloworld.entity.TopicLike;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TopicLikeMapper {
    
    @Insert("INSERT INTO topic_like (topic_id, user_id, create_time) VALUES (#{topicId}, #{userId}, NOW())")
    int insertTopicLike(TopicLike topicLike);
    
    @Delete("DELETE FROM topic_like WHERE topic_id = #{topicId} AND user_id = #{userId}")
    int deleteTopicLike(@Param("topicId") Long topicId, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM topic_like WHERE topic_id = #{topicId} AND user_id = #{userId}")
    int countTopicLike(@Param("topicId") Long topicId, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM topic_like WHERE topic_id = #{topicId}")
    int countTopicLikesByTopicId(@Param("topicId") Long topicId);
}