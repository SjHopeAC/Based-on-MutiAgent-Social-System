package com.example.helloworld.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    
    /**
     * 获取所有举报
     */
    @Select("SELECT id, reporter_id as reporterId, target_id as targetId, target_type as targetType, " +
            "reason, description, status, handler_id as handlerId, handle_remark as handleRemark, " +
            "handle_time as handleTime, create_time as createTime " +
            "FROM report " +
            "ORDER BY create_time DESC")
    List<Map<String, Object>> selectAllReports();
    
    /**
     * 更新举报状态
     */
    @Update("UPDATE report SET " +
            "status = #{status}, " +
            "handler_id = #{handlerId}, " +
            "handle_remark = #{handleRemark}, " +
            "handle_time = NOW() " +
            "WHERE id = #{reportId}")
    int updateReportStatus(@Param("reportId") Long reportId,
                         @Param("status") Integer status,
                         @Param("handlerId") Long handlerId,
                         @Param("handleRemark") String handleRemark);
}
