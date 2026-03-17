package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.Building;
import com.boiler.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 建筑控制器
 * 处理建筑相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/heat-user/building")
@RequiredArgsConstructor
@CrossOrigin
public class BuildingController {

    private final BuildingService buildingService;

    /**
     * 分页获取建筑列表
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param buildingName 建筑名称（模糊查询）
     * @param stationId 热力站ID
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listBuildings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) Long stationId) {
        
        Page<Building> pageResult = buildingService.listBuildings(page, size, buildingName, stationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取建筑详情
     * @param id 建筑ID
     * @return 建筑信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Long id) {
        Building building = buildingService.getBuildingById(id);
        return ResponseEntity.ok(building);
    }

    /**
     * 根据热力站ID获取建筑列表
     * @param stationId 热力站ID
     * @return 建筑列表
     */
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Building>> listBuildingsByStationId(@PathVariable Long stationId) {
        List<Building> buildings = buildingService.listBuildingsByStationId(stationId);
        return ResponseEntity.ok(buildings);
    }

    /**
     * 新增建筑
     * @param building 建筑信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveBuilding(@RequestBody Building building) {
        boolean success = buildingService.saveBuilding(building);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (success) {
            result.put("id", building.getId());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 更新建筑信息
     * @param id 建筑ID
     * @param building 建筑信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBuilding(
            @PathVariable Long id, 
            @RequestBody Building building) {
        building.setId(id);
        boolean success = buildingService.updateBuilding(building);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除建筑
     * @param id 建筑ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBuilding(@PathVariable Long id) {
        boolean success = buildingService.deleteBuilding(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
}
