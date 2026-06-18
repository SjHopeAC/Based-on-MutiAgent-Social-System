package com.example.helloworld.controller;

import com.example.helloworld.entity.AgentAnalysisResult;
import com.example.helloworld.entity.Event;
import com.example.helloworld.entity.Result;
import com.example.helloworld.service.OpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/opinion")
public class OpinionController {
    
    @Autowired
    private OpinionService opinionService;
    
    @GetMapping("/mock-agent-data")
    public Result getMockAgentData() {
        System.out.println("返回模拟的多智能体数据");
        
        AgentAnalysisResult data = new AgentAnalysisResult();
        data.setConfidence(0.9);
        data.setIsPublicOpinion(true);
        data.setCoreEntity("某品牌奶粉");
        data.setEventType("产品质量");
        data.setKeywords(Arrays.asList("奶粉", "质量", "问题"));
        data.setOpinionContent("某品牌奶粉被曝出质量问题");
        data.setFanCount(5000);
        data.setLikeCount(100);
        data.setRepostCount(50);
        
        return Result.success(data);
    }
    
    @PostMapping("/process")
    public Result processOpinion(@RequestBody AgentAnalysisResult result) {
        Map<String, Object> processResult = opinionService.processOpinion(result);
        return Result.success(processResult);
    }
    
    @GetMapping("/events")
    public Result getAllEvents() {
        List<Event> events = opinionService.getAllEvents();
        return Result.success(events);
    }
    
    @PostMapping("/test")
    public Result testHighConfidence() {
        System.out.println("========== 测试: 高置信度 ==========");
        
        AgentAnalysisResult data = new AgentAnalysisResult();
        data.setConfidence(0.9);
        data.setIsPublicOpinion(true);
        data.setCoreEntity("某品牌奶粉");
        data.setEventType("产品质量");
        data.setKeywords(Arrays.asList("奶粉", "质量", "问题"));
        data.setOpinionContent("某品牌奶粉被曝出质量问题");
        data.setFanCount(5000);
        data.setLikeCount(100);
        data.setRepostCount(50);
        
        Map<String, Object> result = opinionService.processOpinion(data);
        
        System.out.println("========== 测试1完成 ==========");
        return Result.success(result);
    }
}
