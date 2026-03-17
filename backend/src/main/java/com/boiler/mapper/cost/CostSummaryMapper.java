package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.CostSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 成本汇总数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对cost_summary表的基本增删改查操作
 * 同时提供自定义查询方法
 */
@Mapper
public interface CostSummaryMapper extends BaseMapper<CostSummary> {
    
    /**
     * 查询指定日期范围内的成本汇总数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param stationIds 热力站ID列表
     * @param costType 成本类型
     * @return 成本汇总列表
     */
    List<CostSummary> selectByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("stationIds") List<Long> stationIds,
            @Param("costType") String costType);
    
    /**
     * 查询指定热力站和日期的成本汇总
     * @param stationId 热力站ID
     * @param statDate 统计日期
     * @param costType 成本类型
     * @param energyType 能源类型
     * @return 成本汇总记录
     */
    CostSummary selectByStationAndDate(
            @Param("stationId") Long stationId,
            @Param("statDate") LocalDate statDate,
            @Param("costType") String costType,
            @Param("energyType") String energyType);
}
