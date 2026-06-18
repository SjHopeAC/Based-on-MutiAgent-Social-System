package com.example.helloworld.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.helloworld.entity.Topic;
import com.example.helloworld.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {


    @Select("SELECT " +
            "topic_id as topicId, " +
            "category_id as categoryId, " +
            "topic_title as topicTitle, " +
            "topic_desc as topicDesc, " +
            "creator_id as creatorId, " +
            "is_hot as isHot, " +
            "status, " +
            "view_count as viewCount, " +
            "like_count as likeCount, " +
            "participant_count as participantCount, " +
            "share_count as shareCount, " +
            "create_time as createTime, " +
            "update_time as updateTime, " +
            "tags " +
            "FROM topic " +
            "WHERE topic_id = #{topicId}")
    Topic selectTopicById(Long topicId);

        /**
         * 插入话题（只插前端传的字段，其余靠数据库默认值）
         * 数据库字段：topic_id(自增)、category_id、topic_title、topic_desc、creator_id、tags
         * 其余字段（is_hot/status/view_count等）数据库自动赋默认值，无需插入
         */
        @Insert("INSERT INTO topic (" +
                "category_id, topic_title, topic_desc, creator_id, tags" +
                ") VALUES (" +
                "#{categoryId}, #{topicTitle}, #{topicDesc}, #{creatorId}, #{tags}" +
                ")")
        int insertTopic(Topic topic); // 返回受影响行数

    /**
     * 步骤1：计算话题热度值，筛选正常状态的TOP5话题
     * 热度值公式：view_count*0.2 + like_count*0.5 + participant_count*0.3
     * 排序规则：先按热度值降序，热度值相同按更新时间降序
     */
    @Select("SELECT topic_id as topicId, category_id as categoryId, topic_title as topicTitle, topic_desc as topicDesc, creator_id as creatorId, " +
            "is_hot as isHot, status, view_count as viewCount, like_count as likeCount, participant_count as participantCount, share_count as shareCount, " +
            "create_time as createTime, update_time as updateTime, tags FROM topic WHERE status = 1 " +
            "ORDER BY (view_count * 0.2 + like_count * 0.5 + participant_count * 0.3) DESC, update_time DESC LIMIT 5")
    List<Topic> selectTop5HotTopicsByScore();

    /**
     * 步骤2-1：重置所有正常话题的is_hot为0（取消之前的热门标记）
     */
    @Update("UPDATE topic SET is_hot = 0 WHERE status = 1")
    void resetAllHotStatus();

    /**
     * 步骤2-2：单独更新单个话题的is_hot为1（循环调用此方法更新TOP5）
     */
    @Update("UPDATE topic SET is_hot = 1 WHERE topic_id = #{topicId} AND status = 1")
    void updateSingleHotStatus(@Param("topicId") Long topicId);

    /**
     * 步骤3：查询标记为热门（is_hot=1）的正常话题
     * 最终返回给前端的热门话题列表
     */
    @Select("SELECT * FROM topic WHERE is_hot = 1 AND status = 1 ORDER BY update_time DESC")
    List<Topic> selectHotTopicsByIsHotFlag();

    // 更新点赞数
    @Update("UPDATE topic SET like_count = like_count + 1 WHERE topic_id = #{topicId}")
    int incrementLikeCount(@Param("topicId") Long topicId);

    // 更新浏览量
    @Update("UPDATE topic SET view_count = view_count + 1 WHERE topic_id = #{topicId}")
    int incrementViewCount(@Param("topicId") Long topicId);

    @Select("SELECT * FROM topic WHERE creator_id = #{userId}")
    List<Topic> getTopicByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM topic")
    List<Topic> getAllTopic();
    
    @Select("SELECT * FROM topic")
    List<Topic> getAllTopicsWithLikeStatus(@Param("currentUserId") Long currentUserId);

    // 获取热门话题
    @Select("SELECT * FROM topic WHERE is_hot = 1 ORDER BY view_count DESC, like_count DESC LIMIT 10")
    List<Topic> selectTrendingTopics();

    // 按条件查询话题
    @Select("SELECT * FROM topic WHERE topic_title LIKE CONCAT('%', #{filter}, '%') OR topic_desc LIKE CONCAT('%', #{filter}, '%') ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<Topic> selectTopicsByFilter(@Param("filter") String filter, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM topic")
    int selectCount();

    // 查询所有标签
    @Select("SELECT tags FROM topic WHERE tags IS NOT NULL AND tags != ''")
    List<String> selectAllTags();

    /**
     * 更新话题点赞数
     * @param topicId 话题ID
     * @param count 点赞变化量（正数表示增加，负数表示减少）
     * @return 更新成功的记录数
     */
    @Update("UPDATE topic SET like_count = like_count + #{count} WHERE topic_id = #{topicId}")
    int updateLikeCount(@Param("topicId") Long topicId, @Param("count") Integer count);

    /**
     * 搜索话题（按标题或描述模糊搜索）
     * @param keyword 搜索关键词
     * @return 话题列表
     */
    @Select("SELECT topic_id as topicId, category_id as categoryId, topic_title as topicTitle, " +
            "topic_desc as topicDesc, creator_id as creatorId, is_hot as isHot, status, " +
            "view_count as viewCount, like_count as likeCount, participant_count as participantCount, share_count as shareCount, " +
            "create_time as createTime, update_time as updateTime, tags " +
            "FROM topic " +
            "WHERE (topic_title LIKE CONCAT('%', #{keyword}, '%') OR topic_desc LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND status = 1 " +
            "ORDER BY create_time DESC " +
            "LIMIT 20")
    List<Topic> searchTopics(@Param("keyword") String keyword);

    @Select("SELECT * FROM topic WHERE status = 1")
    List<Topic> selectAllTopics();

    @Select("SELECT * FROM topic WHERE is_hot = 1 AND status = 1")
    List<Topic> selectHotTopics();

    @Select("SELECT * FROM topic WHERE status = 1 ORDER BY create_time DESC")
    List<Topic> selectNewestTopics();

    @Select("SELECT t.* FROM topic t " +
            "INNER JOIN user_follow uf ON t.creator_id = uf.follow_user_id " +
            "WHERE uf.user_id = #{currentUserId} AND uf.status = 1 AND t.status = 1")
    List<Topic> selectFollowedTopics(@Param("currentUserId") Long currentUserId);

    /**
     * 更新话题状态
     */
    @Update("UPDATE topic SET status = #{status}, update_time = NOW() WHERE topic_id = #{topicId}")
    int updateTopicStatus(@Param("topicId") Long topicId, @Param("status") Integer status);

    /**
     * 更新话题热门状态
     */
    @Update("UPDATE topic SET is_hot = #{isHot}, update_time = NOW() WHERE topic_id = #{topicId}")
    int updateTopicHotStatus(@Param("topicId") Long topicId, @Param("isHot") Integer isHot);
    
    /**
     * 更新话题参与人数（评论数总和）
     */
    @Update("UPDATE topic SET participant_count = #{participantCount}, update_time = NOW() WHERE topic_id = #{topicId}")
    int updateParticipantCount(@Param("topicId") Long topicId, @Param("participantCount") Integer participantCount);

    /**
     * 统计分类下的话题数量
     */
    @Select("SELECT COUNT(*) FROM topic WHERE category_id = #{categoryId}")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}