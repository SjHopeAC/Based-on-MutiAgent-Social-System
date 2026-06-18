package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.Topic;
import com.example.helloworld.mapper.TopicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员话题管理Controller
 */
@RestController
@RequestMapping("/admin/topics")
public class AdminTopicController {
    
    @Autowired
    private TopicMapper topicMapper;
    
    /**
     * 获取话题列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getTopicList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer isHot) {
        try {
            int offset = (page - 1) * size;
            
            List<Topic> topics;
            int total;
            
            // 如果有keyword参数，进行搜索
            if (keyword != null && !keyword.isEmpty()) {
                topics = topicMapper.searchTopics(keyword);
                total = topics.size();
            } else {
                topics = topicMapper.getAllTopic();
                total = topics.size();
            }
            
            // 过滤状态和热门状态
            if (status != null) {
                topics = topics.stream().filter(t -> t.getStatus().equals(status)).collect(Collectors.toList());
                total = topics.size();
            }
            if (isHot != null) {
                topics = topics.stream().filter(t -> t.getIsHot().equals(isHot)).collect(Collectors.toList());
                total = topics.size();
            }
            
            // 分页处理
            int fromIndex = Math.min(offset, topics.size());
            int toIndex = Math.min(offset + size, topics.size());
            List<Topic> pageList = topics.subList(fromIndex, toIndex);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageList);
            result.put("total", total);
            
            return Result.success("获取话题列表成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取话题列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 审核通过话题
     */
    @PutMapping("/{topicId}/approve")
    public Result<String> approveTopic(@PathVariable Long topicId) {
        try {
            int result = topicMapper.updateTopicStatus(topicId, 1);
            if (result > 0) {
                return Result.success("审核通过成功", "审核通过");
            } else {
                return Result.error("审核通过失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("审核通过失败：" + e.getMessage());
        }
    }
    
    /**
     * 设置热门话题
     */
    @PutMapping("/{topicId}/hot")
    public Result<String> setTopicHot(
            @PathVariable Long topicId,
            @RequestBody Map<String, Integer> params) {
        try {
            int isHot = params.get("isHot");
            int result = topicMapper.updateTopicHotStatus(topicId, isHot);
            if (result > 0) {
                return Result.success("设置成功", "设置成功");
            } else {
                return Result.error("设置失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("设置失败：" + e.getMessage());
        }
    }
    
    /**
     * 下架话题
     */
    @DeleteMapping("/{topicId}")
    public Result<String> deleteTopic(@PathVariable Long topicId) {
        try {
            int result = topicMapper.updateTopicStatus(topicId, 3);
            if (result > 0) {
                return Result.success("下架成功", "下架成功");
            } else {
                return Result.error("下架失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("下架失败：" + e.getMessage());
        }
    }

    /**
     * 恢复话题
     */
    @PutMapping("/{topicId}/restore")
    public Result<String> restoreTopic(@PathVariable Long topicId) {
        try {
            int result = topicMapper.updateTopicStatus(topicId, 1);
            if (result > 0) {
                return Result.success("恢复成功", "恢复成功");
            } else {
                return Result.error("恢复失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("恢复失败：" + e.getMessage());
        }
    }
}
