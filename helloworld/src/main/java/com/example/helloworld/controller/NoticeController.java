package com.example.helloworld.controller;

import com.example.helloworld.entity.Result;
import com.example.helloworld.entity.UserNotice;
import com.example.helloworld.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户通知控制器
 */
@RestController
@RequestMapping("/notifications")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 创建通知
     */
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<Boolean> createNotice(@RequestBody UserNotice notice) {
        boolean success = noticeService.addNotice(notice);
        return success ? Result.success("通知创建成功", true) : Result.error("通知创建失败");
    }

    /**
     * 获取通知列表
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<List<UserNotice>> getNotifications(@RequestParam Long userId) {
        List<UserNotice> list = noticeService.getNoticeList(userId, 1, 100);
        return Result.success("通知查询成功", list);
    }

    /**
     * 标记单条通知为已读
     */
    @PutMapping(value = "/{notificationId}/read", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<Boolean> markAsRead(@PathVariable Long notificationId) {
        boolean success = noticeService.markAsRead(notificationId);
        return success ? Result.success("标记已读成功", true) : Result.error("标记已读失败");
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping(value = "/readAll", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<Boolean> markAllAsRead(@RequestParam Long userId) {
        boolean success = noticeService.markAllAsRead(userId);
        return success ? Result.success("全部标记已读成功", true) : Result.error("全部标记已读失败");
    }

    /**
     * 查询未读通知数量
     */
    @GetMapping(value = "/unreadCount", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<Integer> getUnreadCount(@RequestParam Long userId) {
        int count = noticeService.getUnreadCount(userId);
        return Result.success("未读数量查询成功", count);
    }

    /**
     * 清空用户所有通知
     */
    @DeleteMapping(value = "/clearAll", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result<Boolean> clearAllNotifications(@RequestParam Long userId) {
        boolean success = noticeService.clearAllNotifications(userId);
        return success ? Result.success("清空通知成功", true) : Result.error("清空通知失败");
    }
}