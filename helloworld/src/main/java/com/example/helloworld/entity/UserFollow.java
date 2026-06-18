package com.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * 用户关注关系表
 * 存储用户间的单向关注关系（对应微博的“关注/粉丝”逻辑）
 */
@TableName("user_follow") // 数据库表名
public class UserFollow {

    // 主键ID（自增）
    @TableId(type = IdType.AUTO)
    private Long id;

    // 关注人ID（关联user表的id）
    @TableField("user_id")
    private Long userId;

    // 被关注人ID（关联user表的id）
    @TableField("follow_user_id")
    private Long followUserId;

    // 关系状态：0-已取消关注（软删除） 1-正常关注
    @TableField("status")
    private Byte status;

    // 是否互关：0-否 1-是（优化查询效率，避免两次关联查询）
    @TableField("is_mutual")
    private Boolean isMutual;

    // 创建时间（关注时间，MyBatis-Plus自动填充）
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    // ========== getter/setter 方法 ==========
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowUserId() {
        return followUserId;
    }

    public void setFollowUserId(Long followUserId) {
        this.followUserId = followUserId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Boolean getIsMutual() {
        return isMutual;
    }

    public void setIsMutual(Boolean mutual) {
        isMutual = mutual;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}