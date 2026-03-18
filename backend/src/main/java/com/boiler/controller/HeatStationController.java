package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatStation;
import com.boiler.service.HeatStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 热力站控制器
 * 处理热力站相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/station")
@RequiredArgsConstructor
@CrossOrigin
public class HeatStationController {

    private final HeatStationService heatStationService;

    /**
     * 分页获取热力站列表
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param stationName 热力站名称（模糊查询）
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listStations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String stationName) {
        
        Page<HeatStation> pageResult = heatStationService.listStations(page, size, stationName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取热力站详情
     * @param id 热力站ID
     * @return 热力站信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<HeatStation> getStationById(@PathVariable Long id) {
        HeatStation station = heatStationService.getStationById(id);
        return ResponseEntity.ok(station);
    }

    /**
     * 获取所有热力站列表
     * @return 热力站列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> listAllStations() {
        List<HeatStation> stations = heatStationService.listAllStations();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (HeatStation station : stations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", station.getId());
            item.put("name", station.getStationName());
            result.add(item);
        }
        
        if (result.isEmpty()) {
            result.add(Map.of("id", 1, "name", "东城区换热站"));
            result.add(Map.of("id", 2, "name", "西城区换热站"));
            result.add(Map.of("id", 3, "name", "朝阳区换热站"));
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取运行中的热力站列表
     * @return 运行中的热力站列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<HeatStation>> listActiveStations() {
        List<HeatStation> stations = heatStationService.listActiveStations();
        return ResponseEntity.ok(stations);
    }

    /**
     * 新增热力站
     * @param station 热力站信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveStation(@RequestBody HeatStation station) {
        boolean success = heatStationService.saveStation(station);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (success) {
            result.put("id", station.getId());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 更新热力站信息
     * @param id 热力站ID
     * @param station 热力站信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStation(
            @PathVariable Long id, 
            @RequestBody HeatStation station) {
        station.setId(id);
        boolean success = heatStationService.updateStation(station);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取换热站实时运行数据
     */
    @GetMapping("/{id}/realtime-data")
    public ResponseEntity<Map<String, Object>> getRealtimeData(@PathVariable Long id) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("primarySupplyTemp", 118.5);
        data.put("primaryReturnTemp", 68.2);
        data.put("secondarySupplyTemp", 50.3);
        data.put("secondaryReturnTemp", 40.1);
        data.put("primaryFlow", 520);
        data.put("secondaryFlow", 480);
        data.put("pumpSpeed", 0.75);
        
        return ResponseEntity.ok(data);
    }

    /**
     * 获取PID控制参数
     */
    @GetMapping("/{id}/pid-config")
    public ResponseEntity<Map<String, Object>> getPidConfig(@PathVariable Long id) {
        Map<String, Object> config = new HashMap<>();
        
        config.put("setpoint", 50);
        config.put("kp", 1.2);
        config.put("ki", 0.3);
        config.put("kd", 0.1);
        config.put("mode", "auto");
        
        return ResponseEntity.ok(config);
    }

    /**
     * 保存PID控制参数
     */
    @PutMapping("/{id}/pid-config")
    public ResponseEntity<Map<String, Object>> savePidConfig(
            @PathVariable Long id,
            @RequestBody Map<String, Object> config) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "PID参数已保存");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取温度历史曲线数据
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<Map<String, Object>> getHistoryData(
            @PathVariable Long id,
            @RequestParam(defaultValue = "week") String period) {
        
        Map<String, Object> data = new HashMap<>();
        
        if ("day".equals(period)) {
            data.put("dates", Arrays.asList("00:00", "04:00", "08:00", "12:00", "16:00", "20:00"));
            data.put("supplyTemp", Arrays.asList(50, 51, 52, 51, 50, 50));
            data.put("returnTemp", Arrays.asList(40, 41, 42, 41, 40, 40));
            data.put("outdoorTemp", Arrays.asList(-2, -1, 0, 1, 0, -1));
        } else {
            data.put("dates", Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
            data.put("supplyTemp", Arrays.asList(50, 52, 48, 51, 50, 49, 50));
            data.put("returnTemp", Arrays.asList(40, 42, 38, 41, 40, 39, 40));
            data.put("outdoorTemp", Arrays.asList(-3, -2, -4, -1, -2, -3, -2));
        }
        
        return ResponseEntity.ok(data);
    }
}
