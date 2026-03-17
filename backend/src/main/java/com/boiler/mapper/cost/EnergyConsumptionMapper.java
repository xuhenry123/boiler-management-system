package com.boiler.mapper.cost;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boiler.entity.cost.EnergyConsumption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 能耗数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供对energy_consumption表的基本增删改查操作
 * 同时提供自定义查询方法
 */
@Mapper
public interface EnergyConsumptionMapper extends BaseMapper<EnergyConsumption> {
    
    /**
     * 查询指定日期范围内的能耗数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param stationIds 热力站ID列表
     * @param energyType 能源类型
     * @return 能耗列表
     */
    List<EnergyConsumption> selectByDateRangeAndStation(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("stationIds") List<Long> stationIds,
            @Param("energyType") String energyType);
    
    /**
     * 查询指定热力站某月逐日能耗数据
     * @param stationId 热力站ID
     * @param year 年份
     * @param month 月份
     * @param energyType 能源类型
     * @return 能耗列表
     */
    List<EnergyConsumption> selectByStationAndMonth(
            @Param("stationId") Long stationId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("energyType") String energyType);
}
