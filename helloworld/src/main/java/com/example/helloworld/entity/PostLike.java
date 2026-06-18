package com.example.helloworld.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PostLike {
    private Long id;              // 主键ID
    private Long postId;          // 帖子ID
    private Long userId;          // 用户ID
    private Date createTime;      // 创建时间
}