package com.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PostComment {
    private Long commentId;       // 评论ID
    private Long postId;          // 所属帖子ID
    private Long parentId;        // 父评论ID（0=一级评论）
    private Long creatorId;       // 评论用户ID
    private String commentContent;// 评论内容
    private Integer status;       // 状态
    private Integer likeCount;    // 点赞数
    private Date createTime;      // 创建时间
    private Date updateTime;      // 更新时间

    // 回复对象用户ID（用于获取回复的目标用户信息）
    private Long replyToUserId;    // 被回复的用户ID
    private Integer commentType;  //评论类型：1-正向评论，0-待评评论，-1负向评论，-2垃圾评论

    private Boolean appealed;    // 是否已申诉
    private Double confidence;    // 置信度
    private String reasoning;     // 分析推理说明
    private String result;        // 检测结果
    private String source;        // 检测来源

    // 👇 你要求新增的两个扩展字段（数据库不存在）
    @TableField(exist = false)
    private String appealReason;  // 申诉理由

    @TableField(exist = false)
    private Integer appealStatus; // 申诉状态 0-待处理 1-已通过 2-已驳回

    // 扩展字段（多级评论核心）
    @TableField(exist = false)
    private User creator;         // 评论用户信息

    @TableField(exist = false)
    private List<PostComment> children; // 子评论列表（回复当前评论的评论）

    @TableField(exist = false)
    private User replyToUser;     // 回复的目标用户（可选）

    public Integer getCommentType() {
        return commentType;
    }

    public void setCommentType(Integer commentType) {
        this.commentType = commentType;
    }
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(Long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<PostComment> children() {
        return children;
    }

    public void setChildren(List<PostComment> children) {
        this.children = children;
    }

    public User getReplyToUser() {
        return replyToUser;
    }

    public void setReplyToUser(User replyToUser) {
        this.replyToUser = replyToUser;
    }

    public Boolean getAppealed() {
        return appealed;
    }

    public void setAppealed(Boolean appealed) {
        this.appealed = appealed;
    }

    public String getAppealReason() {
        return appealReason;
    }

    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}