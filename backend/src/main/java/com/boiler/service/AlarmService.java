package com.boiler.service;

import java.util.List;
import java.util.Map;

public interface AlarmService {
    int getUnacknowledgedAlarmCount();
    List<Map<String, Object>> getRecentAlarms(int limit);
}
