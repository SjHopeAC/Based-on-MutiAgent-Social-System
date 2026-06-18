package com.example.helloworld.mapper;

import com.example.helloworld.entity.User;
import com.example.helloworld.entity.UserFollow;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserFollowMapper {
    /**
     * 统计关注关系数量
     * @param userId 关注人ID
     * @param followUserId 被关注人ID
     * @return 符合条件的记录数（0=未关注，1=已关注）
     */
    @Select("SELECT COUNT(*) FROM user_follow WHERE user_id = #{userId} AND follow_user_id = #{followUserId} AND status = 1")
    Integer countFollowRelation(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    /**
     * 更新关注状态（软删除）
     * @param userId 关注人ID
     * @param followUserId 被关注人ID
     * @param status 状态：0=取消关注 1=正常关注
     * @return 受影响行数
     */
    @Update("UPDATE user_follow SET status = #{status} WHERE user_id = #{userId} AND follow_user_id = #{followUserId}")
    int updateFollowStatus(@Param("userId") Long userId, @Param("followUserId") Long followUserId, @Param("status") Byte status);

    /**
     * 根据关注人+被关注人查询关系
     * @param userId 关注人ID
     * @param followUserId 被关注人ID
     * @return 关注关系实体
     */
    @Select("SELECT * FROM user_follow WHERE user_id = #{userId} AND follow_user_id = #{followUserId} LIMIT 1")
    UserFollow selectByUserAndFollowUser(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    /**
     * 4. 新增关注关系（替代XML的insert，纯注解）
     * @param userFollow 关注关系实体
     * @return 受影响行数
     */
    @Insert("INSERT INTO user_follow (user_id, follow_user_id, status, is_mutual, create_time) " +
            "VALUES (#{userId}, #{followUserId}, #{status}, #{isMutual}, NOW())")
    int insert(UserFollow userFollow);

    /**
     * 5. 根据ID更新关注关系（替代XML的updateById，纯注解）
     * 注意：参数拆分为单个，避免实体传递的坑
     * @param id 关注关系ID
     * @param status 状态
     * @param isMutual 是否互关
     * @return 受影响行数
     */
    @Update("UPDATE user_follow SET status = #{status}, is_mutual = #{isMutual} WHERE id = #{id}")
    int updateById(@Param("id") Long id, @Param("status") Byte status, @Param("isMutual") Boolean isMutual);

    /**
     * 获取用户关注列表
     * @param userId 用户ID
     * @return 关注的用户列表
     */
    @Select("SELECT u.* FROM user u INNER JOIN user_follow uf ON u.id = uf.follow_user_id WHERE uf.user_id = #{userId} AND uf.status = 1 ORDER BY uf.create_time DESC")
    List<User> selectFollowingList(Long userId);

    /**
     * 获取用户粉丝列表
     * @param userId 用户ID
     * @return 粉丝列表
     */
    @Select("SELECT u.* FROM user u INNER JOIN user_follow uf ON u.id = uf.user_id WHERE uf.follow_user_id = #{userId} AND uf.status = 1 ORDER BY uf.create_time DESC")
    List<User> selectFollowersList(Long userId);

}
