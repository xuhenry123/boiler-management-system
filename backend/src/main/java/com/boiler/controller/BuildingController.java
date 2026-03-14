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

@RestController
@RequestMapping("/api/heat-user/building")
@RequiredArgsConstructor
@CrossOrigin
public class BuildingController {

    private final BuildingService buildingService;

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

    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Long id) {
        Building building = buildingService.getBuildingById(id);
        return ResponseEntity.ok(building);
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Building>> listBuildingsByStationId(@PathVariable Long stationId) {
        List<Building> buildings = buildingService.listBuildingsByStationId(stationId);
        return ResponseEntity.ok(buildings);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBuilding(@PathVariable Long id) {
        boolean success = buildingService.deleteBuilding(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
}
