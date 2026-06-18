package com.example.helloworld.service;

import com.example.helloworld.entity.User;
import java.util.List;

public interface UserFollowService {
    boolean isFollowing(Long currentUserId, Long targetUserId);
    boolean toggleFollow(Long currentUserId, Long targetUserId);
    // 获取用户关注列表
    List<User> getFollowingList(Long userId);
    // 获取用户粉丝列表
    List<User> getFollowersList(Long userId);
}
