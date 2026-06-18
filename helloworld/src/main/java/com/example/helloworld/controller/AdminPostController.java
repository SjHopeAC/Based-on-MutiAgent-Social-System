package com.example.helloworld.controller;

import com.example.helloworld.entity.Post;
import com.example.helloworld.entity.Result;
import com.example.helloworld.mapper.CommentMapper;
import com.example.helloworld.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员帖子管理Controller
 */
@RestController
@RequestMapping("/admin/posts")
public class AdminPostController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;
    
    /**
     * 获取帖子列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            int offset = (page - 1) * size;
            
            List<Post> posts;
            int total;
            
            if (status != null) {
                posts = postMapper.selectPostList(offset, size);
                posts = posts.stream().filter(p -> p.getStatus().equals(status)).collect(Collectors.toList());
                total = posts.size();
            } else {
                posts = postMapper.selectPostList(offset, size);
                total = postMapper.selectPostCount1();
            }
            
            // 如果有keyword参数，进行搜索
            if (keyword != null && !keyword.isEmpty()) {
                posts = postMapper.searchPosts(keyword);
                total = posts.size();
                // 分页处理
                int fromIndex = Math.min(offset, posts.size());
                int toIndex = Math.min(offset + size, posts.size());
                posts = posts.subList(fromIndex, toIndex);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", posts);
            result.put("total", total);
            
            return Result.success("获取帖子列表成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取帖子列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 审核通过帖子
     */
    @PutMapping("/{postId}/approve")
    public Result<String> approvePost(@PathVariable Long postId) {
        try {
            int result = postMapper.updatePostStatus(postId, 1);
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
     * 删除帖子
     */
    @DeleteMapping("/{postId}")
    public Result<String> deletePost(@PathVariable Long postId) {
        try {
            commentMapper.deleteCommentsByPostId(postId);
            int result = postMapper.deletePost(postId);
            if (result > 0) {
                return Result.success("删除成功", "删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 恢复帖子
     */
    @PutMapping("/{postId}/restore")
    public Result<String> restorePost(@PathVariable Long postId) {
        try {
            int result = postMapper.updatePostStatus(postId, 1);
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
    
    /**
     * 更新帖子热门状态
     */
    @PutMapping("/{postId}/hot")
    public Result<String> updatePostHotStatus(@PathVariable Long postId, @RequestBody Map<String, Integer> params) {
        try {
            Integer isHot = params.get("isHot");
            int result = postMapper.updatePostHotStatus(postId, isHot);
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
