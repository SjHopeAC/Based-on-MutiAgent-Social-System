package com.example.helloworld.mapper;

import com.example.helloworld.entity.Event;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EventMapper {
    
    @Select("SELECT * FROM EVENTS WHERE core_entity = #{coreEntity} AND event_type = #{eventType}")
    @Results({
        @Result(property = "eventId", column = "event_id"),
        @Result(property = "coreEntity", column = "core_entity"),
        @Result(property = "eventType", column = "event_type"),
        @Result(property = "keywords", column = "keywords"),
        @Result(property = "summary", column = "summary"),
        @Result(property = "totalInfluence", column = "total_influence"),
        @Result(property = "firstTime", column = "first_time"),
        @Result(property = "lastTime", column = "last_time"),
        @Result(property = "status", column = "STATUS"),
        @Result(property = "severityLevel", column = "severity_level")
    })
    List<Event> selectByEntityAndType(@Param("coreEntity") String coreEntity, @Param("eventType") String eventType);
    
    @Select("SELECT * FROM EVENTS")
    @Results({
        @Result(property = "eventId", column = "event_id"),
        @Result(property = "coreEntity", column = "core_entity"),
        @Result(property = "eventType", column = "event_type"),
        @Result(property = "keywords", column = "keywords"),
        @Result(property = "summary", column = "summary"),
        @Result(property = "totalInfluence", column = "total_influence"),
        @Result(property = "firstTime", column = "first_time"),
        @Result(property = "lastTime", column = "last_time"),
        @Result(property = "status", column = "STATUS"),
        @Result(property = "severityLevel", column = "severity_level")
    })
    List<Event> selectAllEvents();
    
    @Select("SELECT * FROM EVENTS WHERE event_id = #{eventId}")
    @Results({
        @Result(property = "eventId", column = "event_id"),
        @Result(property = "coreEntity", column = "core_entity"),
        @Result(property = "eventType", column = "event_type"),
        @Result(property = "keywords", column = "keywords"),
        @Result(property = "summary", column = "summary"),
        @Result(property = "totalInfluence", column = "total_influence"),
        @Result(property = "firstTime", column = "first_time"),
        @Result(property = "lastTime", column = "last_time"),
        @Result(property = "status", column = "STATUS"),
        @Result(property = "severityLevel", column = "severity_level")
    })
    Event selectByEventId(@Param("eventId") String eventId);
    
    @Insert("INSERT INTO EVENTS (event_id, core_entity, event_type, keywords, summary, total_influence, first_time, last_time, STATUS, severity_level) " +
            "VALUES (#{eventId}, #{coreEntity}, #{eventType}, #{keywords}, #{summary}, #{totalInfluence}, #{firstTime}, #{lastTime}, #{status}, #{severityLevel})")
    void insertEvent(Event event);
    
    @Update("UPDATE EVENTS SET total_influence = #{totalInfluence}, last_time = #{lastTime}, STATUS = #{status}, severity_level = #{severityLevel} " +
            "WHERE event_id = #{eventId}")
    void updateEvent(Event event);
}
