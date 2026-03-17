package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatStation;
import com.boiler.service.HeatStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<HeatStation>> listAllStations() {
        List<HeatStation> stations = heatStationService.listAllStations();
        return ResponseEntity.ok(stations);
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
}
