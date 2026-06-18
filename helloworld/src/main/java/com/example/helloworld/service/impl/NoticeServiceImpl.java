package com.example.helloworld.service.impl;

import com.example.helloworld.entity.UserNotice;
import com.example.helloworld.mapper.UserNoticeMapper;
import com.example.helloworld.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户通知服务实现类
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private UserNoticeMapper userNoticeMapper;

    @Override
    public boolean addNotice(UserNotice notice) {
        // 默认未读
        if (notice.getIsRead() == null) {
            notice.setIsRead(0);
        }
        return userNoticeMapper.insert(notice) > 0;
    }

    @Override
    public List<UserNotice> getNoticeList(Long userId, Integer pageNum, Integer pageSize) {
        // 分页参数校验
        pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        return userNoticeMapper.selectByUserId(userId, offset, pageSize);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return userNoticeMapper.countUnread(userId);
    }

    @Override
    public boolean markAsRead(Long noticeId) {
        return userNoticeMapper.updateReadStatus(noticeId) > 0;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        return userNoticeMapper.updateAllRead(userId) > 0;
    }

    @Override
    public boolean deleteNoticeByTargetAndUser(Long targetId, Long fromUserId, Integer noticeType) {
        return userNoticeMapper.deleteByTargetAndUser(targetId, fromUserId, noticeType) > 0;
    }

    @Override
    public boolean clearAllNotifications(Long userId) {
        return userNoticeMapper.deleteAllByUserId(userId) > 0;
    }
}