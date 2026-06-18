package com.example.helloworld.service;

import com.example.helloworld.entity.UserNotice;

import java.util.List;

/**
 * 用户通知服务接口
 */
public interface NoticeService {

    /**
     * 新增通知
     */
    boolean addNotice(UserNotice notice);

    /**
     * 分页查询用户通知
     */
    List<UserNotice> getNoticeList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询未读通知数量
     */
    int getUnreadCount(Long userId);

    /**
     * 标记单条通知为已读
     */
    boolean markAsRead(Long noticeId);

    /**
     * 标记所有通知为已读
     */
    boolean markAllAsRead(Long userId);

    /**
     * 删除指定类型的通知
     */
    boolean deleteNoticeByTargetAndUser(Long targetId, Long fromUserId, Integer noticeType);

    /**
     * 清空用户所有通知
     */
    boolean clearAllNotifications(Long userId);
}