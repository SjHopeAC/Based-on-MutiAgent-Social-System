package com.example.helloworld.controller;

import com.example.helloworld.entity.Post;
import com.example.helloworld.entity.Result;
import com.example.helloworld.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    // 1. 新增帖子
    @PostMapping("/create")
    public Result<Boolean> createPost(@RequestBody Post post) {
        try {
            if (post.getTopicId() == null || post.getCreatorId() == null || post.getPostContent() == null) {
                return Result.error("帖子参数不完整");
            }
            boolean success = postService.createPost(post);
            return success ? Result.success("发帖成功", true) : Result.error("发帖失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发帖异常：" + e.getMessage());
        }
    }

    // 2. 根据ID查询帖子详情
    @GetMapping("/{postId}")
    public Result<Post> getPostById(@PathVariable Long postId, @RequestParam(required = false) Long currentUserId) {
        try {
            Post post = postService.getPostById(postId, currentUserId);
            if (post == null) {
                return Result.error("帖子不存在或已删除");
            }
            return Result.success("查询成功", post);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询异常：" + e.getMessage());
        }
    }

    // 3. 分页查询所有帖子
    @GetMapping("/list")
    public Result<List<Post>> getPostList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long currentUserId,
            @RequestParam(required = false) Integer isHot,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer followed) {
        try {
            List<Post> postList = postService.getPostList(pageNum, pageSize, currentUserId, isHot, orderBy, followed);
            return Result.success("查询成功", postList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询异常：" + e.getMessage());
        }
    }

    // 4. 根据话题ID查询帖子
    @GetMapping("/topic/{topicId}")
    public Result<List<Post>> getPostByTopicId(@PathVariable Long topicId) {
        try {
            List<Post> postList = postService.getPostByTopicId(topicId);
            return Result.success("查询成功", postList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询异常：" + e.getMessage());
        }
    }

    // 5. 帖子点赞/取消点赞
    @PutMapping("/like/{postId}")
    public Result<Boolean> updatePostLike(@PathVariable Long postId, @RequestParam Integer count, @RequestParam Long userId) {
        try {
            postService.updatePostLikeCount(postId, count, userId);
            return Result.success("操作成功", true);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作异常：" + e.getMessage());
        }
    }

    // 6. 增加帖子浏览量
    @PostMapping("/view/{postId}")
    public Result<Boolean> increaseViewCount(@PathVariable Long postId) {
        try {
            postService.incrementViewCount(postId);
            return Result.success("浏览量更新成功", true);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作异常：" + e.getMessage());
        }
    }

    //根据页数，页码，话题，是否要选热门的去找帖子
    @GetMapping("/select")
    public Result<Map<String, Object>> selectPost(
            @RequestParam(defaultValue = "1") Integer page,          // 匹配前端page参数
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize, // 匹配前端pagesize参数
            @RequestParam(required = false) Long topic,              // 匹配前端topic参数
            @RequestParam(required = false) String ishot             // 匹配前端ishot参数
    ) {
        try {
            // 处理ishot参数（String转Boolean，兼容true/false/1/0）
            Boolean isHot = null;
            if (ishot != null) {
                isHot = "true".equalsIgnoreCase(ishot) || "1".equals(ishot);
            }

            // 调用Service执行查询
            Map<String, Object> result = postService.selectPostList(page, pageSize, topic, isHot);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    // 根据用户ID查询帖子
    @GetMapping("/user/{userId}")
    public Result<List<Post>> getPostByUserId(@PathVariable Long userId) {
        try {
            List<Post> postList = postService.getPostByUserId(userId);
            return Result.success("查询成功", postList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 获取帖子列表并包含当前用户的关注状态
     * @param page 页码
     * @param topic 话题ID（可选）
     * @param ishot 是否热门（可选）
     * @param currentUserId 当前登录用户ID（可选）
     * @return 帖子列表和总数
     */
    @GetMapping("/selectWithFollow")
    public Result<Map<String, Object>> selectPostWithFollow(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long topic,
            @RequestParam(required = false) String ishot,
            @RequestParam(required = false) Long currentUserId
    ) {
        try {
            // 处理ishot参数（String转Boolean，兼容true/false/1/0）
            Boolean isHot = null;
            if (ishot != null) {
                isHot = "true".equalsIgnoreCase(ishot) || "1".equals(ishot);
            }

            // 调用Service执行查询
            Map<String, Object> result = postService.selectPostListWithFollowStatus(page, pageSize, topic, isHot, currentUserId);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    // 删除帖子
    @DeleteMapping("/{postId}")
    public Result<Boolean> deletePost(@PathVariable Long postId) {
        try {
            boolean success = postService.deletePost(postId);
            return success ? Result.success("删除成功", true) : Result.error("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除异常：" + e.getMessage());
        }
    }
    
    // 编辑帖子
    @PutMapping("/{postId}")
    public Result<Boolean> updatePost(@PathVariable Long postId, @RequestBody Post post) {
        try {
            post.setPostId(postId);
            boolean success = postService.updatePost(post);
            return success ? Result.success("编辑成功", true) : Result.error("编辑失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("编辑异常：" + e.getMessage());
        }
    }
}