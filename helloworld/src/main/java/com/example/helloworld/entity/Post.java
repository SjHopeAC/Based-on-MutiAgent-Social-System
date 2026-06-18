package com.example.helloworld.entity;

import lombok.Data;
import java.util.Date;

/**
 * 帖子实体类（关联话题/分类/用户）
 * 对应表：post
 */
@Data
public class Post {
    private Long postId;          // 帖子ID（主键）
    private String postImgUrl;    // 帖子图片URL
    private Long topicId;         // 所属话题ID
    private Integer categoryId;   // 所属分类ID
    private Long creatorId;       // 创建者ID
    private String postContent;   // 帖子内容
    private Integer status;       // 状态：1-正常，2-审核中，3-已删除，4-违规
    private Integer likeCount;    // 点赞数
    private Integer commentCount; // 评论数
    private Integer viewCount; //  浏览量

    private Integer shareCount;    // 分享次数

    private Date createTime;      // 创建时间
    private Date updateTime;      // 更新时间

    // 扩展字段（非数据库字段，用于前端展示）
    private User creator;         // 发帖用户信息
    private Boolean isLiked;       // 当前用户是否已点赞（非数据库字段）

    private Integer isHot;

    public Integer getIsHot() {
        return isHot;
    }

    public void setIsHot(Integer isHot) {
        this.isHot = isHot;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPostImgUrl() {
        return postImgUrl;
    }

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public Long getUserId() {return creatorId;    }
}