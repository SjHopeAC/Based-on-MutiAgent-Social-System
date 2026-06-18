package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员举报管理Controller
 */
@RestController
@RequestMapping("/admin/reports")
public class AdminReportController {
    
    @Autowired
    private com.example.helloworld.mapper.ReportMapper reportMapper;
    
    /**
     * 获取举报列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getReportList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer targetType) {
        try {
            int offset = (page - 1) * size;
            
            List<Map<String, Object>> reports;
            int total;
            
            reports = reportMapper.selectAllReports();
            
            // 过滤状态和类型
            if (status != null) {
                reports = reports.stream().filter(r -> r.get("status").equals(status)).collect(Collectors.toList());
            }
            if (targetType != null) {
                reports = reports.stream().filter(r -> r.get("targetType").equals(targetType)).collect(Collectors.toList());
            }
            
            total = reports.size();
            
            // 分页处理
            int fromIndex = Math.min(offset, reports.size());
            int toIndex = Math.min(offset + size, reports.size());
            List<Map<String, Object>> pageList = reports.subList(fromIndex, toIndex);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageList);
            result.put("total", total);
            
            return Result.success("获取举报列表成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取举报列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理举报
     */
    @PutMapping("/{reportId}/handle")
    public Result<String> handleReport(
            @PathVariable Long reportId,
            @RequestBody Map<String, Object> params) {
        try {
            Integer status = (Integer) params.get("status");
            String handleRemark = (String) params.get("handleRemark");
            Long handlerId = (Long) params.get("handlerId");
            
            // 如果没有传递handlerId，使用默认值1（管理员ID）
            if (handlerId == null) {
                handlerId = 1L;
            }
            
            int result = reportMapper.updateReportStatus(reportId, status, handlerId, handleRemark);
            if (result > 0) {
                return Result.success("处理成功", "处理成功");
            } else {
                return Result.error("处理失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理失败：" + e.getMessage());
        }
    }
}
