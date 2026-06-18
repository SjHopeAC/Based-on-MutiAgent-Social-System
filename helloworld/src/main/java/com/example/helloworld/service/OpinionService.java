package com.example.helloworld.service;

import com.example.helloworld.entity.AgentAnalysisResult;
import com.example.helloworld.entity.Event;
import com.example.helloworld.entity.PendingReview;
import com.example.helloworld.mapper.EventMapper;
import com.example.helloworld.mapper.PendingReviewMapper;
import com.example.helloworld.util.BertClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OpinionService {
    
    @Autowired
    private EventMapper eventMapper;
    
    @Autowired
    private PendingReviewMapper pendingReviewMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Map<String, Object> processOpinion(AgentAnalysisResult result) {
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("开始处理舆情...");

        //结果非舆情，不处理
        if (!result.getIsPublicOpinion()) {
            response.put("action", "ignored");
            response.put("msg", "非舆情");
            return response;
        }
        
        double conf = result.getConfidence() != null ? result.getConfidence() : 0;
        //置信度低，加入待审核队列
        if (conf < 0.8) {
            System.out.println("置信度低，加入待审核表");
            saveToPendingReview(result, null);
            response.put("action", "pending_review");
            response.put("msg", "已加入待审核列表");
            return response;
        }

        //创建搜索集
        List<Event> searchResults = new ArrayList<>();
        
        System.out.println("步骤1: 按实体+事件类型搜索");
        List<Event> step1 = eventMapper.selectByEntityAndType(result.getCoreEntity(), result.getEventType());
        if (step1 != null) searchResults.addAll(step1);
        
        System.out.println("步骤2: 按关键词搜索(>=2个)");
        List<Event> allEvents = eventMapper.selectAllEvents();
        if (allEvents != null) {
            for (Event e : allEvents) {
                if (countKeywordMatch(e, result.getKeywords()) >= 2) {
                    if (!contains(searchResults, e)) {
                        searchResults.add(e);
                    }
                }
            }
        }
        
        System.out.println("步骤3: BERT检查(/best_match)");
        List<Map<String, Object>> candidates = new ArrayList<>();
        for (Event e : searchResults) {
            Map<String, Object> cand = new HashMap<>();
            cand.put("type", "舆情摘要");
            cand.put("id", e.getEventId());
            cand.put("content", e.getSummary());
            candidates.add(cand);
        }
        
        Map<String, Object> bertResult = BertClient.callBestMatch(result.getOpinionContent(), candidates);
        Object bestMatchObj = bertResult.get("best_match");

        //找到数据库中舆情的处理方法
        if (bestMatchObj != null) {
            System.out.println("找到已有事件，更新");
            Map<String, Object> bestMatch = (Map<String, Object>) bestMatchObj;
            String eventId = (String) bestMatch.get("id");
            Event event = eventMapper.selectByEventId(eventId);
            
            if (event != null) {
                BigDecimal add = calculateInfluence(result);
                event.setTotalInfluence(event.getTotalInfluence().add(add));
                event.setLastTime(LocalDateTime.now());
                updateLevel(event);
                eventMapper.updateEvent(event);
                
                response.put("action", "updated");
                response.put("event", event);
            }
        }
        else {
            System.out.println("无匹配，全局搜索");
            
            List<Map<String, Object>> globalCandidates = mockGlobalSearch(result.getCoreEntity());
            Map<String, Object> batchResult = BertClient.callBatchSimilarity(result.getOpinionContent(), globalCandidates);
            List<Map<String, Object>> matchedList = (List<Map<String, Object>>) batchResult.get("matched_list");
            
            BigDecimal totalInf = BigDecimal.ZERO;
            
            if (matchedList != null && !matchedList.isEmpty()) {
                for (Map<String, Object> match : matchedList) {
                    BigDecimal inf = calculateInfluence(result);
                    totalInf = totalInf.add(inf);
                }
            } else {
                totalInf = calculateInfluence(result);
            }
            
            Event newEvent = createEvent(result, totalInf);
            response.put("action", "created");
            response.put("event", newEvent);
        }
        
        return response;
    }
    
    private void saveToPendingReview(AgentAnalysisResult result, String sourceEventId) {
        PendingReview review = new PendingReview();
        review.setReviewId("R" + System.currentTimeMillis());
        review.setSourceEventId(sourceEventId);
        review.setConfidence(new BigDecimal(result.getConfidence()).setScale(4, RoundingMode.HALF_UP));
        review.setCoreEntity(result.getCoreEntity());
        review.setEventType(result.getEventType());
        
        try {
            review.setKeywords(objectMapper.writeValueAsString(result.getKeywords()));
        } catch (Exception e) {
            review.setKeywords("[]");
        }
        
        review.setSummary(result.getOpinionContent());
        review.setReasoning("多智能体置信度低于80%");
        review.setCommentId("C" + System.currentTimeMillis());
        review.setCommentText(result.getOpinionContent());
        review.setCommentTime(LocalDateTime.now());
        review.setUserId("U" + System.currentTimeMillis());
        review.setUserFollowers(result.getFanCount());
        review.setUserRegisterDays(365);
        review.setReviewStatus("待审核");
        review.setCreatedTime(LocalDateTime.now());
        
        pendingReviewMapper.insertPendingReview(review);
        System.out.println("已保存到待审核表: " + review.getReviewId());
    }
    
    private boolean contains(List<Event> list, Event event) {
        for (Event e : list) {
            if (e.getEventId().equals(event.getEventId())) return true;
        }
        return false;
    }
    
    private int countKeywordMatch(Event event, List<String> newKeywords) {
        int count = 0;
        try {
            List<String> eventKeywords = objectMapper.readValue(event.getKeywords(), List.class);
            for (String kw : newKeywords) {
                if (eventKeywords.contains(kw)) count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    
    private List<Map<String, Object>> mockGlobalSearch(String coreEntity) {
        System.out.println("模拟全局搜索: " + coreEntity);
        List<Map<String, Object>> list = new ArrayList<>();
        
        Map<String, Object> post = new HashMap<>();
        post.put("type", "帖子");
        post.put("id", "p1");
        post.put("content", coreEntity + "质量问题");
        list.add(post);
        
        Map<String, Object> topic = new HashMap<>();
        topic.put("type", "话题");
        topic.put("id", "t1");
        topic.put("content", coreEntity + "讨论");
        list.add(topic);
        
        Map<String, Object> comment = new HashMap<>();
        comment.put("type", "评论");
        comment.put("id", "c1");
        comment.put("content", "今天天气好");
        list.add(comment);
        
        return list;
    }
    
    private BigDecimal calculateInfluence(AgentAnalysisResult result) {
        int fans = result.getFanCount() != null ? result.getFanCount() : 0;
        int likes = result.getLikeCount() != null ? result.getLikeCount() : 0;
        int reposts = result.getRepostCount() != null ? result.getRepostCount() : 0;
        
        double fanFactor = 1.0 + fans / 1000.0;
        double logFactor = Math.log(fanFactor);
        double interaction = likes * 0.5 + reposts * 0.5;
        
        double inf = 1.0 + logFactor * interaction;
        return new BigDecimal(inf).setScale(2, RoundingMode.HALF_UP);
    }
    
    private Event createEvent(AgentAnalysisResult result, BigDecimal influence) {
        Event event = new Event();
        event.setEventId("E" + System.currentTimeMillis());
        event.setCoreEntity(result.getCoreEntity());
        event.setEventType(result.getEventType());
        
        try {
            event.setKeywords(objectMapper.writeValueAsString(result.getKeywords()));
        } catch (Exception e) {
            event.setKeywords("[]");
        }
        
        event.setSummary(result.getOpinionContent());
        event.setTotalInfluence(influence);
        event.setFirstTime(LocalDateTime.now());
        event.setLastTime(LocalDateTime.now());
        
        updateLevel(event);
        eventMapper.insertEvent(event);
        return event;
    }
    
    private void updateLevel(Event event) {
        BigDecimal inf = event.getTotalInfluence();
        if (inf.compareTo(new BigDecimal("500")) >= 0) {
            event.setSeverityLevel(3);
            event.setStatus("紧急");
        } else if (inf.compareTo(new BigDecimal("200")) >= 0) {
            event.setSeverityLevel(2);
            event.setStatus("预警");
        } else {
            event.setSeverityLevel(1);
            event.setStatus("关注");
        }
    }
    
    public List<Event> getAllEvents() {
        return eventMapper.selectAllEvents();
    }
}
