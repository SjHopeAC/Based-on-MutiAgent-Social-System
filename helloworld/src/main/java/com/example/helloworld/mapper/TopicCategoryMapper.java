package com.example.helloworld.mapper;

import com.example.helloworld.entity.TopicCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 话题分类Mapper（适配前端 /api/topic/categories 接口）
 */
@Mapper // 标记为MyBatis Mapper接口，SpringBoot会自动扫描并创建实现类
public interface TopicCategoryMapper {

    /**
     * 查询热门话题分类（适配前端获取分类列表的接口）
     * @return 分类列表
     */
    @Select("SELECT * FROM topic_category  WHERE is_hot = 1")
    List<TopicCategory> getHotTopicCategories();

    /**
     * 查询所有话题分类（适配前端获取分类列表的接口）
     * @return 分类列表
     */
    @Select("SELECT * FROM topic_category")
    List<TopicCategory> getAllTopicCategories();

    /**
     * 更新分类话题数（+1）
     * @param categoryId 分类ID
     * @return 受影响的行数
     */
    @Update("UPDATE topic_category SET topic_count = topic_count + 1 WHERE category_id = #{categoryId}")
    int incrementTopicCount(@Param("categoryId") Integer categoryId);
}