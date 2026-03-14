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

@RestController
@RequestMapping("/api/station")
@RequiredArgsConstructor
@CrossOrigin
public class HeatStationController {

    private final HeatStationService heatStationService;

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

    @GetMapping("/{id}")
    public ResponseEntity<HeatStation> getStationById(@PathVariable Long id) {
        HeatStation station = heatStationService.getStationById(id);
        return ResponseEntity.ok(station);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HeatStation>> listAllStations() {
        List<HeatStation> stations = heatStationService.listAllStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/active")
    public ResponseEntity<List<HeatStation>> listActiveStations() {
        List<HeatStation> stations = heatStationService.listActiveStations();
        return ResponseEntity.ok(stations);
    }

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
