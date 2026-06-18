package com.example.helloworld.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Event {
    private String eventId;
    private String coreEntity;
    private String eventType;
    private String keywords;
    private String summary;
    private BigDecimal totalInfluence;
    private LocalDateTime firstTime;
    private LocalDateTime lastTime;
    private String status;
    private Integer severityLevel;
    
    public Event() {
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
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
    
    public BigDecimal getTotalInfluence() {
        return totalInfluence;
    }
    
    public void setTotalInfluence(BigDecimal totalInfluence) {
        this.totalInfluence = totalInfluence;
    }
    
    public LocalDateTime getFirstTime() {
        return firstTime;
    }
    
    public void setFirstTime(LocalDateTime firstTime) {
        this.firstTime = firstTime;
    }
    
    public LocalDateTime getLastTime() {
        return lastTime;
    }
    
    public void setLastTime(LocalDateTime lastTime) {
        this.lastTime = lastTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getSeverityLevel() {
        return severityLevel;
    }
    
    public void setSeverityLevel(Integer severityLevel) {
        this.severityLevel = severityLevel;
    }
}
