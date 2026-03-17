package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.EfficiencyMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 能效指标数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对efficiency_metrics表的基本增删改查操作
 * 同时提供自定义查询方法
 */
@Mapper
public interface EfficiencyMetricsMapper extends BaseMapper<EfficiencyMetrics> {
    
    /**
     * 查询指定日期范围内的能效指标数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param stationIds 热力站ID列表
     * @return 能效指标列表
     */
    List<EfficiencyMetrics> selectByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("stationIds") List<Long> stationIds);
    
    /**
     * 查询指定热力站的能效指标
     * @param stationId 热力站ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 能效指标列表
     */
    List<EfficiencyMetrics> selectByStation(
            @Param("stationId") Long stationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
