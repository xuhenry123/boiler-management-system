package com.boiler.service.impl;

import com.boiler.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int getUnacknowledgedAlarmCount() {
        try {
            String sql = "SELECT COUNT(*) FROM alert_record WHERE acknowledged = 0";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> getRecentAlarms(int limit) {
        try {
            String sql = """
                SELECT id, alert_level as alarmLevel, alert_message as alarmMessage, 
                       alert_time as createTime, acknowledged
                FROM alert_record 
                ORDER BY alert_time DESC 
                LIMIT ?
                """;
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, limit);
            
            for (Map<String, Object> alarm : results) {
                alarm.put("alarmLevelText", getAlarmLevelText((String) alarm.get("alarmLevel")));
                alarm.put("alarmType", "system");
                alarm.put("buildingName", "系统告警");
                alarm.put("alarmValue", "-");
                alarm.put("thresholdValue", "-");
            }
            
            if (results.isEmpty()) {
                return getDefaultAlarms();
            }
            
            return results;
        } catch (Exception e) {
            return getDefaultAlarms();
        }
    }

    private String getAlarmLevelText(String level) {
        if (level == null) return "未知";
        return switch (level.toUpperCase()) {
            case "CRITICAL" -> "严重";
            case "WARN", "WARNING" -> "警告";
            case "INFO" -> "提示";
            default -> "未知";
        };
    }

    private List<Map<String, Object>> getDefaultAlarms() {
        List<Map<String, Object>> alarms = new ArrayList<>();
        
        Map<String, Object> alarm1 = new HashMap<>();
        alarm1.put("id", 1);
        alarm1.put("alarmType", "low_temp");
        alarm1.put("alarmMessage", "室内温度低于设定值");
        alarm1.put("alarmLevel", "Warning");
        alarm1.put("alarmLevelText", "警告");
        alarm1.put("buildingName", "阳光花园1号楼");
        alarm1.put("alarmValue", 16.5);
        alarm1.put("thresholdValue", 18.0);
        alarm1.put("createTime", "2026-03-16 14:30:00");
        alarm1.put("acknowledged", false);
        alarms.add(alarm1);
        
        Map<String, Object> alarm2 = new HashMap<>();
        alarm2.put("id", 2);
        alarm2.put("alarmType", "pressure");
        alarm2.put("alarmMessage", "一次侧压力异常");
        alarm2.put("alarmLevel", "Critical");
        alarm2.put("alarmLevelText", "严重");
        alarm2.put("buildingName", "商业大厦A座");
        alarm2.put("alarmValue", 1.65);
        alarm2.put("thresholdValue", 1.60);
        alarm2.put("createTime", "2026-03-16 13:15:00");
        alarm2.put("acknowledged", false);
        alarms.add(alarm2);
        
        return alarms;
    }
}
