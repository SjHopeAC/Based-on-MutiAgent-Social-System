package com.example.helloworld.service.impl;

import com.example.helloworld.entity.User;
import com.example.helloworld.entity.UserFollow;
import com.example.helloworld.entity.UserNotice;
import com.example.helloworld.mapper.UserFollowMapper;
import com.example.helloworld.mapper.UserMapper;
import com.example.helloworld.service.UserFollowService;
import com.example.helloworld.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserFollowServiceImpl implements UserFollowService {
    @Autowired
    private UserFollowMapper userFollowMapper; // 注入Mapper

    @Autowired
    private UserMapper userMapper; // 注入Mapper
    
    @Autowired
    private NoticeService noticeService; // 注入NoticeService
    /**
     * 切换关注状态（已关注则取消，未关注则关注）
     * @param currentUserId 当前登录用户ID（关注人）
     * @param targetUserId 被关注/取消关注的用户ID
     * @return true=操作成功，false=操作失败
     */
    @Override
    public boolean toggleFollow(Long currentUserId, Long targetUserId) {
        // 1. 基础校验：空值+自关注拦截
        if (currentUserId == null || targetUserId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("不能关注自己");
        }

        // 2. 查询当前有效关注关系（仅status=1视为已关注）
        Integer followCount = userFollowMapper.countFollowRelation(currentUserId, targetUserId);
        boolean isFollowing = followCount != null && followCount > 0;

        // 3. 已关注 → 执行取消关注（软删除）
        if (isFollowing) {
            int updateCount = userFollowMapper.updateFollowStatus(currentUserId, targetUserId, (byte) 0);
            if (updateCount > 0) {
                // 同步更新用户关注数/粉丝数（-1）
                userMapper.decrFollowCount(currentUserId);
                userMapper.decrFansCount(targetUserId);
                // 更新对方的互关状态（如果对方关注了当前用户，需把对方的isMutual改为false）
                updateMutualStatus(targetUserId, currentUserId);
                
                // 创建取消关注通知
                User currentUser = userMapper.selectById(currentUserId);
                UserNotice notice = new UserNotice();
                notice.setUserId(targetUserId);
                notice.setFromUserId(currentUserId);
                notice.setNoticeType(1); // 1-关注通知
                notice.setTargetType(0); // 0-用户
                notice.setTargetId(currentUserId);
                notice.setContent(currentUser.getNickname() + " 取消关注了你");
                notice.setIsRead(0);
                noticeService.addNotice(notice);
                
                return true;
            }
        }
        // 4. 未关注 → 恢复已有记录 或 新增记录
        else {
            // 4.1 先查是否有已取消的记录（不管status，仅查user_id+follow_user_id组合）
            UserFollow existFollow = userFollowMapper.selectByUserAndFollowUser(currentUserId, targetUserId);

            if (existFollow != null) {
                // 4.2 有取消记录 → 恢复关注（拆分为三个参数调用updateById）
                Long followId = existFollow.getId(); // 参数1：关注关系ID
                Byte newStatus = (byte) 1; // 参数2：恢复为有效关注状态
                // 重新校验互关状态：目标用户是否关注当前用户
                Integer mutualCount = userFollowMapper.countFollowRelation(targetUserId, currentUserId);
                Boolean newIsMutual = mutualCount != null && mutualCount > 0; // 参数3：互关状态

                // 核心修改：传入三个单独参数，替代原实体传参
                userFollowMapper.updateById(followId, newStatus, newIsMutual);
            } else {
                // 4.3 无记录 → 新增关注关系
                UserFollow userFollow = new UserFollow();
                userFollow.setUserId(currentUserId);
                userFollow.setFollowUserId(targetUserId);
                userFollow.setStatus((byte) 1);
                // 校验互关状态
                Integer mutualCount = userFollowMapper.countFollowRelation(targetUserId, currentUserId);
                userFollow.setIsMutual(mutualCount != null && mutualCount > 0);
                userFollowMapper.insert(userFollow);
            }

            // 同步更新用户关注数/粉丝数（+1）
            userMapper.incrFollowCount(currentUserId);
            userMapper.incrFansCount(targetUserId);
            
            // 创建关注通知
            User currentUser = userMapper.selectById(currentUserId);
            UserNotice notice = new UserNotice();
            notice.setUserId(targetUserId);
            notice.setFromUserId(currentUserId);
            notice.setNoticeType(1); // 1-关注通知
            notice.setTargetType(0); // 0-用户
            notice.setTargetId(currentUserId);
            notice.setContent(currentUser.getNickname() + " 关注了你");
            notice.setIsRead(0);
            noticeService.addNotice(notice);
            
            return true;
        }

        // 5. 兜底：操作未生效（如取消关注时无对应记录）
        return false;
    }


    // 辅助方法：更新互关状态
    private void updateMutualStatus(Long userId, Long followUserId) {
        UserFollow follow = userFollowMapper.selectByUserAndFollowUser(userId, followUserId);
        if (follow != null) {
            follow.setIsMutual(false);
            userFollowMapper.updateById(follow.getId(), follow.getStatus(), follow.getIsMutual());
        }
    }

    /**
     * 判断当前用户是否关注了目标用户
     * @param currentUserId 当前登录用户ID（关注人）
     * @param targetUserId 目标用户ID（被关注人）
     * @return true=已关注，false=未关注
     */
    @Override
    public boolean isFollowing(Long currentUserId, Long targetUserId) {
        // 防空校验
        if (currentUserId == null || targetUserId == null) {
            return false;
        }
        // 查询user_follow表：是否存在 关注人=currentUserId、被关注人=targetUserId、状态=1（正常关注）的记录
        Integer count = userFollowMapper.countFollowRelation(currentUserId, targetUserId);
        return count != null && count > 0;
    }

    @Override
    public List<User> getFollowingList(Long userId) {
        try {
            return userFollowMapper.selectFollowingList(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> getFollowersList(Long userId) {
        try {
            return userFollowMapper.selectFollowersList(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
