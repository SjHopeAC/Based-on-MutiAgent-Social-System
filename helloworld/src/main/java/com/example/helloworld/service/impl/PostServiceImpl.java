package com.example.helloworld.service.impl;

import com.example.helloworld.entity.Post;
import com.example.helloworld.entity.PostLike;
import com.example.helloworld.entity.User;
import com.example.helloworld.mapper.PostMapper;
import com.example.helloworld.mapper.PostLikeMapper;

import com.example.helloworld.service.PostService;
import com.example.helloworld.service.UserFollowService;
import com.example.helloworld.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserFollowService userFollowService;
    @Autowired
    private PostLikeMapper postLikeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPost(Post post) {
        boolean success = postMapper.insertPost(post) > 0;
        if (success && post.getCreatorId() != null) {
            userService.incrementPostCount(post.getCreatorId());
        }
        return success;
    }

    @Override
    public Post getPostById(Long postId, Long currentUserId) {
        Post post = postMapper.selectPostById(postId);
        if (post != null) {
            User creator = userService.getUserById(post.getCreatorId());
            post.setCreator(creator);
            
            if (currentUserId != null) {
                int likeCount = postLikeMapper.countPostLike(postId, currentUserId);
                post.setIsLiked(likeCount > 0);
            }
        }
        return post;
    }

    @Override
    public List<Post> getPostList(Integer pageNum, Integer pageSize, Long currentUserId, Integer isHot, String orderBy, Integer followed) {
        List<Post> postList;
        
        if (followed != null && followed == 1) {
            postList = postMapper.selectFollowedPosts(currentUserId);
        } else if (isHot != null && isHot == 1) {
            postList = postMapper.selectHotPosts();
        } else if ("createTime".equals(orderBy)) {
            postList = postMapper.selectNewestPosts();
        } else {
            Integer offset = (pageNum - 1) * pageSize;
            postList = postMapper.selectPostList(offset, pageSize);
        }
        
        postList.forEach(post -> {
            User creator = userService.getUserById(post.getCreatorId());
            post.setCreator(creator);
            
            if (currentUserId != null) {
                int likeCount = postLikeMapper.countPostLike(post.getPostId(), currentUserId);
                post.setIsLiked(likeCount > 0);
            }
        });
        return postList;
    }

    @Override
    public List<Post> getPostByTopicId(Long topicId) {
        List<Post> postList = postMapper.selectPostByTopicId(topicId);
        postList.forEach(post -> {
            User creator = userService.getUserById(post.getCreatorId());
            post.setCreator(creator);
        });
        return postList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementCommentCount(Long postId) {
        postMapper.incrementCommentCount(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long postId) {
        postMapper.incrementViewCount(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePostLikeCount(Long postId, Integer count, Long userId) {
        postMapper.updateLikeCount(postId, count);
        
        if (count > 0) {
            // 点赞逻辑：先检查是否已经点赞，避免重复插入
            int existingCount = postLikeMapper.countPostLike(postId, userId);
            if (existingCount == 0) {
                PostLike postLike = new PostLike();
                postLike.setPostId(postId);
                postLike.setUserId(userId);
                postLikeMapper.insertPostLike(postLike);
            }
        } else {
            // 取消点赞逻辑
            postLikeMapper.deletePostLike(postId, userId);
        }
    }

    /**
     * 分页查询帖子（匹配前端参数，无动态SQL标签）
     */
    public Map<String, Object> selectPostList(
            Integer page,       // 前端page参数
            Integer pageSize,   // 前端pagesize参数
            Long topicId,       // 前端topic参数
            Boolean isHot       // 前端ishot参数
    ) {
        // 1. 参数校验（避免异常）
        page = (page == null || page < 1) ? 1 : page;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        Integer offset = (page - 1) * pageSize;

        // 2. 处理空参数（保证SQL中NULL判断生效）
        if (topicId != null && topicId <= 0) {
            topicId = null; // 无效topicId置空，不参与过滤
        }

        // 3. 调用Mapper执行纯原生SQL
        List<Post> postList = postMapper.selectPostByParams(offset, pageSize, topicId, isHot);
        Integer total = postMapper.selectPostCount(topicId, isHot);
        
        // 补充每个帖子的用户信息
        postList.forEach(post -> {
            User creator = userService.getUserById(post.getCreatorId());
            post.setCreator(creator);
        });

        // 4. 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", postList);
        result.put("total", total);
        return result;
    }

    @Override
    public List<Post> getPostByUserId(Long userId) {
        List<Post> postList = postMapper.selectPostByUserId(userId);
        // 补充每个帖子的用户信息
        postList.forEach(post -> {
            User creator = userService.getUserById(post.getCreatorId());
            post.setCreator(creator);
        });
        return postList;
    }
    
    /**
     * 获取帖子列表并包含当前用户的关注状态
     */
    @Override
    public Map<String, Object> selectPostListWithFollowStatus(
            Integer page,       // 前端page参数
            Integer pageSize,   // 前端pagesize参数
            Long topicId,       // 前端topic参数
            Boolean isHot,      // 前端ishot参数
            Long currentUserId  // 当前登录用户ID
    ) {
        // 1. 参数校验（避免异常）
        page = (page == null || page < 1) ? 1 : page;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        Integer offset = (page - 1) * pageSize;

        // 2. 处理空参数（保证SQL中NULL判断生效）
        if (topicId != null && topicId <= 0) {
            topicId = null; // 无效topicId置空，不参与过滤
        }

        // 3. 调用Mapper执行纯原生SQL
        List<Post> postList = postMapper.selectPostByParams(offset, pageSize, topicId, isHot);
        Integer total = postMapper.selectPostCount(topicId, isHot);
        
        // 补充每个帖子的用户信息和关注状态
        postList.forEach(post -> {
            User creator = userService.getUserById(post.getCreatorId());
            post.setCreator(creator);
            
            // 检查当前用户是否关注了帖子创建者
            if (currentUserId != null && creator != null) {
                boolean isFollowing = userFollowService.isFollowing(currentUserId, creator.getId());
                creator.setFollowing(isFollowing);
            }
        });

        // 4. 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", postList);
        result.put("total", total);
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePost(Long postId) {
        return postMapper.deletePost(postId) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePost(Post post) {
        return postMapper.updatePost(post) > 0;
    }
}