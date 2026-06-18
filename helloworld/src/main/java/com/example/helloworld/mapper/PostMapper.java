package com.example.helloworld.mapper;

import com.example.helloworld.entity.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {
    // 1. 新增帖子（包含postImgUrl）
    @Insert("INSERT INTO post (topic_id, category_id, creator_id, post_content, post_img_url) " +
            "VALUES (#{topicId}, #{categoryId}, #{creatorId}, #{postContent}, #{postImgUrl})")
    int insertPost(Post post);

    //2. 根据ID查询帖子（包含postImgUrl）
    @Select("SELECT post_id as postId, topic_id as topicId, category_id as categoryId, " +
            "creator_id as creatorId, post_content as postContent, post_img_url as postImgUrl, view_count as viewCount, " +
            "status, like_count as likeCount, comment_count as commentCount, share_count as shareCount, " +
            "create_time as createTime, update_time as updateTime " +
            "FROM post WHERE post_id = #{postId} AND status = 1")
    Post selectPostById(Long postId);

    //3. 查询所有帖子（分页+条件）
    @Select("SELECT post_id as postId, topic_id as topicId, category_id as categoryId, " +
            "creator_id as creatorId, post_content as postContent, post_img_url as postImgUrl, view_count as viewCount, status, like_count as likeCount, " +
            "comment_count as commentCount, share_count as shareCount, is_hot as isHot, create_time as createTime, update_time as updateTime " +
            "FROM post WHERE status = 1 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Post> selectPostList(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    //4. 根据话题ID查询帖子
    @Select("SELECT post_id as postId, topic_id as topicId, category_id as categoryId, " +
            "creator_id as creatorId, post_content as postContent, post_img_url as postImgUrl, view_count as viewCount, status, like_count as likeCount, " +
            "comment_count as commentCount, share_count as shareCount, create_time as createTime, update_time as updateTime " +
            "FROM post WHERE topic_id = #{topicId} AND status = 1 " +
            "ORDER BY create_time DESC")
    List<Post> selectPostByTopicId(Long topicId);

    // 5. 更新帖子评论数（+1）
    @Update("UPDATE post SET comment_count = comment_count + 1 WHERE post_id = #{postId}")
    int incrementCommentCount(Long postId);

    // 6. 更新帖子点赞数
    @Update("UPDATE post SET like_count = like_count + #{count} WHERE post_id = #{postId}")
    int updateLikeCount(@Param("postId") Long postId, @Param("count") Integer count);

    //  浏览量 +1
    @Update("UPDATE post SET view_count = view_count + 1 WHERE post_id = #{postId}")
    int incrementViewCount(Long postId);

        /**
         * 查询帖子列表（纯原生SQL，条件通过参数传递）
         */
        @Select("SELECT post_id as postId, topic_id as topicId, category_id as categoryId, " +
                "creator_id as creatorId, post_content as postContent, post_img_url as postImgUrl, " +
                "view_count as viewCount, status, like_count as likeCount, comment_count as commentCount, share_count as shareCount, " +
                "create_time as createTime, update_time as updateTime " +
                "FROM post " +
                "WHERE status = 1 " +
                "AND (#{topicId} IS NULL OR topic_id = #{topicId}) " + // 有topicId则过滤，无则忽略
                "ORDER BY " +
                "CASE WHEN #{isHot} = true THEN (view_count + like_count * 2 + comment_count * 3) ELSE create_time END DESC " + // 动态排序
                "LIMIT #{offset}, #{pageSize}") // 分页
        List<Post> selectPostByParams(
                @Param("offset") Integer offset,
                @Param("pageSize") Integer pageSize,
                @Param("topicId") Long topicId,
                @Param("isHot") Boolean isHot
        );

        /**
         * 查询总条数（纯原生SQL）
         */
        @Select("SELECT COUNT(*) FROM post " +
                "WHERE status = 1 " +
                "AND (#{topicId} IS NULL OR topic_id = #{topicId})" ) // 有topicId则过滤，无则忽略
        Integer selectPostCount(
                @Param("topicId") Long topicId,
                @Param("isHot") Boolean isHot
        );
        
        /**
         * 根据用户ID查询帖子
         */
        @Select("SELECT post_id as postId, topic_id as topicId, category_id as categoryId, " +
                "creator_id as creatorId, post_content as postContent, post_img_url as postImgUrl, " +
                "view_count as viewCount, status, like_count as likeCount, " +
                "comment_count as commentCount, share_count as shareCount, create_time as createTime, update_time as updateTime " +
                "FROM post WHERE creator_id = #{userId} AND status = 1 " +
                "ORDER BY create_time DESC")
        List<Post> selectPostByUserId(Long userId);

    /**
     * 搜索帖子（按内容模糊搜索）
     * @param keyword 搜索关键词
     * @return 帖子列表
     */
    @Select("SELECT post_id as postId, topic_id as topicId, category_id as categoryId, " +
            "creator_id as creatorId, post_content as postContent, post_img_url as postImgUrl, " +
            "view_count as viewCount, status, like_count as likeCount, " +
            "comment_count as commentCount, is_hot as isHot, create_time as createTime, update_time as updateTime " +
            "FROM post " +
            "WHERE post_content LIKE CONCAT('%', #{keyword}, '%') " +
            "AND status = 1 " +
            "ORDER BY create_time DESC " +
            "LIMIT 20")
    List<Post> searchPosts(@Param("keyword") String keyword);

    @Select("SELECT * FROM post WHERE status = 1")
    List<Post> selectAllPosts();

    @Select("SELECT * FROM post WHERE is_hot = 1 AND status = 1")
    List<Post> selectHotPosts();

    @Select("SELECT * FROM post WHERE status = 1 ORDER BY create_time DESC")
    List<Post> selectNewestPosts();

    @Select("SELECT p.* FROM post p " +
            "INNER JOIN user_follow uf ON p.creator_id = uf.follow_user_id " +
            "WHERE uf.user_id = #{currentUserId} AND p.status = 1")
    List<Post> selectFollowedPosts(@Param("currentUserId") Long currentUserId);

    /**
     * 删除帖子（软删除，将status设置为3）
     */
    @Update("UPDATE post SET status = 3 WHERE post_id = #{postId}")
    int deletePost(Long postId);

    /**
     * 编辑帖子
     */
    @Update("UPDATE post SET post_content = #{postContent}, post_img_url = #{postImgUrl}, update_time = NOW() WHERE post_id = #{postId}")
    int updatePost(Post post);

    /**
     * 更新帖子状态
     */
    @Update("UPDATE post SET status = #{status}, update_time = NOW() WHERE post_id = #{postId}")
    int updatePostStatus(@Param("postId") Long postId, @Param("status") Integer status);

    /**
     * 获取帖子总数
     */
    @Select("SELECT COUNT(*) FROM post WHERE status = 1")
    int selectPostCount1();
    
    /**
     * 更新帖子热门状态
     */
    @Update("UPDATE post SET is_hot = #{isHot}, update_time = NOW() WHERE post_id = #{postId}")
    int updatePostHotStatus(@Param("postId") Long postId, @Param("isHot") Integer isHot);

    /**
     * 根据话题ID查询所有帖子（用于计算参与人数）
     */
    @Select("SELECT post_id as postId, topic_id as topicId, category_id as categoryId, " +
            "creator_id as creatorId, post_content as postContent, post_img_url as postImgUrl, view_count as viewCount, status, like_count as likeCount, " +
            "comment_count as commentCount, share_count as shareCount, create_time as createTime, update_time as updateTime " +
            "FROM post WHERE topic_id = #{topicId} AND status = 1")
    List<Post> selectPostsByTopicId(Long topicId);

}