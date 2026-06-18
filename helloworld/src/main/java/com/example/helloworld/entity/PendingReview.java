package com.example.helloworld.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PendingReview {
    private String reviewId;
    private String sourceEventId;
    private BigDecimal confidence;
    private String coreEntity;
    private String eventType;
    private String keywords;
    private String summary;
    private String reasoning;
    private String commentId;
    private String commentText;
    private LocalDateTime commentTime;
    private String userId;
    private Integer userFollowers;
    private Integer userRegisterDays;
    private String reviewStatus;
    private String reviewResult;
    private String reviewComment;
    private String reviewerId;
    private LocalDateTime createdTime;
    
    public PendingReview() {
    }
    
    public String getReviewId() {
        return reviewId;
    }
    
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    
    public String getSourceEventId() {
        return sourceEventId;
    }
    
    public void setSourceEventId(String sourceEventId) {
        this.sourceEventId = sourceEventId;
    }
    
    public BigDecimal getConfidence() {
        return confidence;
    }
    
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    
    public String getCoreEntity() {
        return coreEntity;
    }
    
    public void setCoreEntity(String coreEntity) {
        this.coreEntity = coreEntity;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getKeywords() {
        return keywords;
    }
    
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getReasoning() {
        return reasoning;
    }
    
    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
    
    public String getCommentId() {
        return commentId;
    }
    
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    
    public String getCommentText() {
        return commentText;
    }
    
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
    
    public LocalDateTime getCommentTime() {
        return commentTime;
    }
    
    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Integer getUserFollowers() {
        return userFollowers;
    }
    
    public void setUserFollowers(Integer userFollowers) {
        this.userFollowers = userFollowers;
    }
    
    public Integer getUserRegisterDays() {
        return userRegisterDays;
    }
    
    public void setUserRegisterDays(Integer userRegisterDays) {
        this.userRegisterDays = userRegisterDays;
    }
    
    public String getReviewStatus() {
        return reviewStatus;
    }
    
    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
    
    public String getReviewResult() {
        return reviewResult;
    }
    
    public void setReviewResult(String reviewResult) {
        this.reviewResult = reviewResult;
    }
    
    public String getReviewComment() {
        return reviewComment;
    }
    
    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
    
    public String getReviewerId() {
        return reviewerId;
    }
    
    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
