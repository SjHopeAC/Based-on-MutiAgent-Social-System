package com.example.helloworld.mapper;

import com.example.helloworld.entity.PendingReview;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PendingReviewMapper {
    
    @Insert("INSERT INTO pending_reviews (review_id, source_event_id, confidence, core_entity, event_type, keywords, summary, reasoning, comment_id, comment_text, comment_time, user_id, user_followers, user_register_days, review_status, created_time) " +
            "VALUES (#{reviewId}, #{sourceEventId}, #{confidence}, #{coreEntity}, #{eventType}, #{keywords}, #{summary}, #{reasoning}, #{commentId}, #{commentText}, #{commentTime}, #{userId}, #{userFollowers}, #{userRegisterDays}, #{reviewStatus}, #{createdTime})")
    void insertPendingReview(PendingReview review);
}
