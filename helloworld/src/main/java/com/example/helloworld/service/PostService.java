// PostService 接口
package com.example.helloworld.service;

import com.example.helloworld.entity.Post;
import java.util.List;
import java.util.Map;

public interface PostService {
    // 新增帖子
    boolean createPost(Post post);
    // 根据ID查询帖子
    Post getPostById(Long postId, Long currentUserId);
    // 分页查询所有帖子
    List<Post> getPostList(Integer pageNum, Integer pageSize, Long currentUserId, Integer isHot, String orderBy, Integer followed);
    // 根据话题ID查询帖子
    List<Post> getPostByTopicId(Long topicId);
    // 更新帖子评论数
    void incrementCommentCount(Long postId);
    // 更新帖子点赞数
    void updatePostLikeCount(Long postId, Integer count, Long userId);
    // 增加帖子浏览量
    void incrementViewCount(Long postId);

    Map<String, Object> selectPostList(
            Integer page,       // 前端page参数
            Integer pageSize,   // 前端pagesize参数
            Long topicId,       // 前端topic参数
            Boolean isHot       // 前端ishot参数
    );
    
    // 根据用户ID查询帖子
    List<Post> getPostByUserId(Long userId);
    
    // 获取帖子列表并包含当前用户的关注状态
    Map<String, Object> selectPostListWithFollowStatus(
            Integer page,       // 前端page参数
            Integer pageSize,   // 前端pagesize参数
            Long topicId,       // 前端topic参数
            Boolean isHot,      // 前端ishot参数
            Long currentUserId  // 当前登录用户ID
    );
    
    // 删除帖子
    boolean deletePost(Long postId);
    
    // 编辑帖子
    boolean updatePost(Post post);
}

