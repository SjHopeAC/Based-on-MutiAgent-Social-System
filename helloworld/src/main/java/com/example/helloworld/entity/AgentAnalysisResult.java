package com.example.helloworld.entity;

import java.util.List;

public class AgentAnalysisResult {
    private Double confidence;
    private Boolean isPublicOpinion;
    private String coreEntity;
    private String eventType;
    private List<String> keywords;
    private String opinionContent;
    private Integer fanCount;
    private Integer likeCount;
    private Integer repostCount;
    
    public AgentAnalysisResult() {
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    public Boolean getIsPublicOpinion() {
        return isPublicOpinion;
    }
    
    public void setIsPublicOpinion(Boolean isPublicOpinion) {
        this.isPublicOpinion = isPublicOpinion;
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
    
    public List<String> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    
    public String getOpinionContent() {
        return opinionContent;
    }
    
    public void setOpinionContent(String opinionContent) {
        this.opinionContent = opinionContent;
    }
    
    public Integer getFanCount() {
        return fanCount;
    }
    
    public void setFanCount(Integer fanCount) {
        this.fanCount = fanCount;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getRepostCount() {
        return repostCount;
    }
    
    public void setRepostCount(Integer repostCount) {
        this.repostCount = repostCount;
    }
}
