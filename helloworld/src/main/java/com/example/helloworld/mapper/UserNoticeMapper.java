package com.example.helloworld.mapper;

import com.example.helloworld.entity.UserNotice;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户通知Mapper（纯注解版，无XML）
 */
@Mapper
public interface UserNoticeMapper {

    /**
     * 新增通知
     */
    @Insert("INSERT INTO user_notice (user_id, from_user_id, notice_type, target_type, target_id, content, is_read, create_time) " +
            "VALUES (#{userId}, #{fromUserId}, #{noticeType}, #{targetType}, #{targetId}, #{content}, #{isRead}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserNotice notice);

    /**
     * 分页查询用户通知
     */
    @Select("SELECT n.*, u.nickname as fromUserNickname, u.avatar as fromUserAvatar " +
            "FROM user_notice n " +
            "LEFT JOIN user u ON n.from_user_id = u.id " +
            "WHERE n.user_id = #{userId} " +
            "ORDER BY n.create_time DESC " +
            "LIMIT #{offset}, #{size}")
    List<UserNotice> selectByUserId(@Param("userId") Long userId,
                                    @Param("offset") Integer offset,
                                    @Param("size") Integer size);

    /**
     * 查询未读通知数量
     */
    @Select("SELECT COUNT(*) FROM user_notice WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(@Param("userId") Long userId);

    /**
     * 标记单条通知为已读
     */
    @Update("UPDATE user_notice SET is_read = 1 WHERE id = #{noticeId}")
    int updateReadStatus(@Param("noticeId") Long noticeId);

    /**
     * 标记用户所有通知为已读
     */
    @Update("UPDATE user_notice SET is_read = 1 WHERE user_id = #{userId}")
    int updateAllRead(@Param("userId") Long userId);

    /**
     * 删除指定类型的通知
     */
    @Delete("DELETE FROM user_notice WHERE target_id = #{targetId} AND from_user_id = #{fromUserId} AND notice_type = #{noticeType}")
    int deleteByTargetAndUser(@Param("targetId") Long targetId, @Param("fromUserId") Long fromUserId, @Param("noticeType") Integer noticeType);

    /**
     * 删除用户所有通知
     */
    @Delete("DELETE FROM user_notice WHERE user_id = #{userId}")
    int deleteAllByUserId(@Param("userId") Long userId);
}