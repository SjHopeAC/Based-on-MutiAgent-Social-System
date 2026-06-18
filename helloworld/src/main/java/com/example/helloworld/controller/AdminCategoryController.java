package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.mapper.CategoryMapper;
import com.example.helloworld.mapper.TopicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员分类管理Controller
 */
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TopicMapper topicMapper;
    
    /**
     * 获取分类列表
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getCategoryList() {
        try {
            List<Map<String, Object>> categories = categoryMapper.selectAllCategories();
            return Result.success("获取分类列表成功", categories);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取分类列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增分类
     */
    @PostMapping
    public Result<String> addCategory(@RequestBody Map<String, Object> params) {
        try {
            String categoryName = (String) params.get("categoryName");
            String description = (String) params.get("description");
            Integer sortOrder = (Integer) params.get("sortOrder");
            Integer status = (Integer) params.get("status");
            
            int result = categoryMapper.insertCategory(categoryName, description, sortOrder, status);
            if (result > 0) {
                return Result.success("新增成功", "新增成功");
            } else {
                return Result.error("新增失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("新增失败：" + e.getMessage());
        }
    }
    
    /**
     * 编辑分类
     */
    @PutMapping("/{categoryId}")
    public Result<String> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody Map<String, Object> params) {
        try {
            String categoryName = (String) params.get("categoryName");
            String description = (String) params.get("description");
            Integer sortOrder = (Integer) params.get("sortOrder");
            Integer status = (Integer) params.get("status");
            
            int result = categoryMapper.updateCategory(categoryId, categoryName, description, sortOrder, status);
            if (result > 0) {
                return Result.success("编辑成功", "编辑成功");
            } else {
                return Result.error("编辑失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("编辑失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新分类状态
     */
//    @PutMapping("/{categoryId}/status")
//    public Result<String> updateCategoryStatus(
//            @PathVariable Long categoryId,
//            @RequestBody Map<String, Integer> params) {
//        try {
//            int status = params.get("status");
//            if (status == 0) {
//                int topicCount = topicMapper.countByCategoryId(categoryId);
//                if (topicCount > 0) {
//                    return Result.error("该分类下有 " + topicCount + " 个话题，请先删除话题后再禁用");
//                }
//            }
//            int result = categoryMapper.updateCategoryStatus(categoryId, status);
//            if (result > 0) {
//                return Result.success("更新成功", "更新成功");
//            } else {
//                return Result.error("更新失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("更新失败：" + e.getMessage());
//        }
//    }
    
    /**
     * 更新分类热门状态
     */
    @PutMapping("/{categoryId}/hot")
    public Result<String> updateCategoryHotStatus(
            @PathVariable Long categoryId,
            @RequestBody Map<String, Boolean> params) {
        try {
            Boolean isHot = params.get("isHot");
            int result = categoryMapper.updateCategoryHotStatus(categoryId, isHot);
            if (result > 0) {
                return Result.success("更新热门状态成功", "更新成功");
            } else {
                return Result.error("更新热门状态失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新热门状态失败：" + e.getMessage());
        }
    }
}
