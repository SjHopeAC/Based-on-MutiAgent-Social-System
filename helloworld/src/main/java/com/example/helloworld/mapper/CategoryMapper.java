package com.example.helloworld.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper {
    
    /**
     * 获取所有分类
     */
    @Select("SELECT category_id as id, category_name as categoryName, is_hot as isHot, topic_count as topicCount, 1 as status " +
            "FROM topic_category " +
            "ORDER BY category_id ASC")
    List<Map<String, Object>> selectAllCategories();
    
    /**
     * 新增分类
     */
    @Insert("INSERT INTO topic_category (category_name, is_hot, topic_count) " +
            "VALUES (#{categoryName}, 0, 0)")
    int insertCategory(@Param("categoryName") String categoryName, 
                      @Param("description") String description,
                      @Param("sortOrder") Integer sortOrder,
                      @Param("status") Integer status);
    
    /**
     * 更新分类
     */
    @Update("UPDATE topic_category SET " +
            "category_name = #{categoryName} " +
            "WHERE category_id = #{categoryId}")
    int updateCategory(@Param("categoryId") Long categoryId,
                      @Param("categoryName") String categoryName,
                      @Param("description") String description,
                      @Param("sortOrder") Integer sortOrder,
                      @Param("status") Integer status);

    /**
     * 更新分类热门状态
     */
    @Update("UPDATE topic_category SET is_hot = #{isHot} WHERE category_id = #{categoryId}")
    int updateCategoryHotStatus(@Param("categoryId") Long categoryId, @Param("isHot") Boolean isHot);

    /**
     * 更新分类话题数
     */
    @Update("UPDATE topic_category SET topic_count = #{topicCount} WHERE category_id = #{categoryId}")
    int updateTopicCount(@Param("categoryId") Long categoryId, @Param("topicCount") Integer topicCount);

    /**
     * 分类话题数+1
     */
    @Update("UPDATE topic_category SET topic_count = topic_count + 1 WHERE category_id = #{categoryId}")
    int incrementTopicCount(@Param("categoryId") Long categoryId);
}
