package com.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("topic") // 严格对应数据库表名
public class Topic {
    @TableId(type = IdType.AUTO) // 主键自增，匹配数据库topic_id
    private Long topicId;          // 话题ID（主键），对应topic_id

    private Integer categoryId;    // 所属分类ID（关联topic_category表），对应category_id

    private String topicTitle;     // 话题标题（非空），对应topic_title

    private String topicDesc;      // 话题描述/内容，对应topic_desc

    private Long creatorId;        // 创建者ID（关联user表），对应creator_id

    private Integer isHot;         // 是否热门：0-否，1-是（数据库默认0），对应is_hot

    private Integer status;        // 状态：1-正常，2-审核中，3-已下架（数据库默认1），对应status

    private Integer viewCount;     // 浏览量（数据库默认0），对应view_count

    private Integer likeCount;     // 点赞数（数据库默认0），对应like_count

    private Integer participantCount; // 参与人数（数据库默认0），对应participant_count

    private Integer shareCount;     // 分享次数（数据库默认0），对应share_count

    private Date createTime;       // 创建时间（数据库自动生成），对应create_time

    private Date updateTime;       // 更新时间（数据库自动更新），对应update_time

    private String tags;           // 标签（逗号分隔），对应tags

    @TableField(exist = false)
    private Boolean isLiked;       // 是否已点赞（前端状态，不对应数据库字段）

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

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicDesc() {
        return topicDesc;
    }

    public void setTopicDesc(String topicDesc) {
        this.topicDesc = topicDesc;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getIsHot() {
        return isHot;
    }

    public void setIsHot(Integer isHot) {
        this.isHot = isHot;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}