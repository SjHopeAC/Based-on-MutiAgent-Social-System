package com.example.helloworld.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 私信实体类
 */
@Data
public class PrivateMessage {
    /**
     * 私信ID
     */
    private Long id;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 私信类型：1=直接私信，2=分享话题，3=分享帖子
     */
    private Integer type;
    
    /**
     * 私信内容，直接私信时使用
     */
    private String content;
    
    /**
     * 目标ID，分享话题时为话题ID，分享帖子时为帖子ID
     */
    private Long targetId;
    
    /**
     * 状态：0=未读，1=已读
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    
    // 扩展字段，用于前端显示
    private User sender;
    private User receiver;
    private Topic topic;
    private Post post;
}