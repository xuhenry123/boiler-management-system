package com.boiler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@CrossOrigin
public class EquipmentController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getEquipment(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> equipmentList = new ArrayList<>();
        
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM boiler WHERE 1=1");
            List<Object> params = new ArrayList<>();
            
            if (status != null) {
                sql.append(" AND status = ?");
                params.add(status);
            }
            
            sql.append(" ORDER BY id");
            
            List<Map<String, Object>> boilers = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            
            for (Map<String, Object> boiler : boilers) {
                Map<String, Object> equip = new HashMap<>();
                equip.put("id", boiler.get("id"));
                equip.put("code", boiler.get("boiler_code"));
                equip.put("name", boiler.get("boiler_name"));
                equip.put("type", "boiler");
                equip.put("station", "东城区换热站");
                equip.put("status", boiler.get("status"));
                equip.put("efficiency", 95);
                equip.put("runtime", 7200);
                equipmentList.add(equip);
            }
            
            String valveSql = "SELECT * FROM valve_device ORDER BY id";
            List<Map<String, Object>> valves = jdbcTemplate.queryForList(valveSql);
            
            for (Map<String, Object> valve : valves) {
                Map<String, Object> equip = new HashMap<>();
                equip.put("id", valve.get("id"));
                equip.put("code", valve.get("valve_code"));
                equip.put("name", valve.get("valve_name"));
                equip.put("type", "valve");
                equip.put("station", "东城区换热站");
                equip.put("status", valve.get("status"));
                equip.put("efficiency", 85);
                equip.put("runtime", 5000);
                equipmentList.add(equip);
            }
            
            if (equipmentList.isEmpty()) {
                equipmentList = getDefaultEquipment();
            }
            
        } catch (Exception e) {
            equipmentList = getDefaultEquipment();
        }
        
        result.put("data", equipmentList);
        result.put("total", equipmentList.size());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/maintenance-reminders")
    public ResponseEntity<List<Map<String, Object>>> getMaintenanceReminders() {
        List<Map<String, Object>> reminders = new ArrayList<>();
        
        reminders.add(Map.of(
            "id", 1,
            "equipment", "1号循环泵",
            "maintainType", "定期保养",
            "dueDate", "2026-03-20",
            "daysLeft", 5
        ));
        
        reminders.add(Map.of(
            "id", 2,
            "equipment", "2号燃气锅炉",
            "maintainType", "年度检修",
            "dueDate", "2026-03-25",
            "daysLeft", 10
        ));
        
        reminders.add(Map.of(
            "id", 3,
            "equipment", "1号板式换热器",
            "maintainType", "清洗维护",
            "dueDate", "2026-03-18",
            "daysLeft", 3
        ));
        
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/type-distribution")
    public ResponseEntity<List<Map<String, Object>>> getTypeDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        distribution.add(Map.of("value", 3, "name", "锅炉"));
        distribution.add(Map.of("value", 6, "name", "循环泵"));
        distribution.add(Map.of("value", 4, "name", "换热器"));
        distribution.add(Map.of("value", 12, "name", "调节阀"));
        
        return ResponseEntity.ok(distribution);
    }

    private List<Map<String, Object>> getDefaultEquipment() {
        List<Map<String, Object>> list = new ArrayList<>();
        
        Map<String, Object> e1 = new HashMap<>();
        e1.put("id", 1);
        e1.put("code", "BLR001");
        e1.put("name", "1号燃气锅炉");
        e1.put("type", "boiler");
        e1.put("station", "东城区换热站");
        e1.put("status", 1);
        e1.put("efficiency", 95);
        e1.put("runtime", 7200);
        list.add(e1);
        
        Map<String, Object> e2 = new HashMap<>();
        e2.put("id", 2);
        e2.put("code", "BLR002");
        e2.put("name", "2号燃气锅炉");
        e2.put("type", "boiler");
        e2.put("station", "东城区换热站");
        e2.put("status", 1);
        e2.put("efficiency", 93);
        e2.put("runtime", 6800);
        list.add(e2);
        
        Map<String, Object> e3 = new HashMap<>();
        e3.put("id", 3);
        e3.put("code", "PUMP001");
        e3.put("name", "1号循环泵");
        e3.put("type", "pump");
        e3.put("station", "东城区换热站");
        e3.put("status", 1);
        e3.put("efficiency", 88);
        e3.put("runtime", 8000);
        list.add(e3);
        
        return list;
    }
}
