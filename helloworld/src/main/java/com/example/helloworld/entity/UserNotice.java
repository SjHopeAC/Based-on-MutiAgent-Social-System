package com.example.helloworld.entity;

import lombok.Data;
import java.util.Date;

/**
 * 用户通知实体类（包含私信通知）
 */
@Data
public class UserNotice {
    /**
     * 通知ID
     */
    private Long id;

    /**
     * 接收通知的用户ID
     */
    private Long userId;

    /**
     * 触发通知的用户ID（关注/点赞/评论/私信人）
     */
    private Long fromUserId;

    /**
     * 通知类型：1-关注 2-点赞 3-评论 4-私信 5-系统通知
     */
    private Integer noticeType;

    /**
     * 关联对象类型：1-帖子 2-评论 3-私信
     */
    private Integer targetType;

    /**
     * 关联对象ID（帖子ID/评论ID/私信ID）
     */
    private Long targetId;

    /**
     * 通知内容/摘要
     */
    private String content;

    /**
     * 已读状态：0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private Date createTime;

    // ===================== 扩展字段（前端展示用） =====================
    /**
     * 触发通知的用户昵称（冗余字段，避免联表）
     */
    private String fromUserNickname;

    /**
     * 触发通知的用户头像（冗余字段）
     */
    private String fromUserAvatar;

    // ===================== 通知类型常量 =====================
    /**
     * 关注通知
     */
    public static final int NOTICE_TYPE_FOLLOW = 1;

    /**
     * 点赞通知
     */
    public static final int NOTICE_TYPE_LIKE = 2;

    /**
     * 评论通知
     */
    public static final int NOTICE_TYPE_COMMENT = 3;

    /**
     * 私信通知
     */
    public static final int NOTICE_TYPE_PRIVATE_MSG = 4;

    /**
     * 系统通知
     */
    public static final int NOTICE_TYPE_SYSTEM = 5;

    // ===================== 关联对象类型常量 =====================
    /**
     * 关联帖子
     */
    public static final int TARGET_TYPE_POST = 1;

    /**
     * 关联评论
     */
    public static final int TARGET_TYPE_COMMENT = 2;

    /**
     * 关联私信
     */
    public static final int TARGET_TYPE_PRIVATE_MSG = 3;
}