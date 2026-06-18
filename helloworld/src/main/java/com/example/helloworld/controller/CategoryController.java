package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 分类Controller
 */
@RestController
public class CategoryController {
    
    @Autowired
    private com.example.helloworld.mapper.CategoryMapper categoryMapper;
    
    /**
     * 获取分类列表
     */
    @GetMapping("/topic/categories")
    public Result<List<Map<String, Object>>> getCategoryList() {
        try {
            List<Map<String, Object>> categories = categoryMapper.selectAllCategories();
            return Result.success("获取分类列表成功", categories);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取分类列表失败：" + e.getMessage());
        }
    }
}
