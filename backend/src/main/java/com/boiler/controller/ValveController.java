package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.ValveDevice;
import com.boiler.service.ValveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阀门设备控制器
 * 处理阀门设备相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/valve")
@RequiredArgsConstructor
@CrossOrigin
public class ValveController {

    private final ValveService valveService;

    /**
     * 分页获取阀门设备列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listValves(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String valveName,
            @RequestParam(required = false) Long nodeId) {
        
        Page<ValveDevice> pageResult = valveService.listValves(page, size, valveName, nodeId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取阀门设备
     */
    @GetMapping("/{id}")
    public ResponseEntity<ValveDevice> getValveById(@PathVariable Long id) {
        ValveDevice valve = valveService.getValveById(id);
        return ResponseEntity.ok(valve);
    }

    /**
     * 根据节点ID获取阀门列表
     */
    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<ValveDevice>> listValvesByNodeId(@PathVariable Long nodeId) {
        List<ValveDevice> valves = valveService.listValvesByNodeId(nodeId);
        return ResponseEntity.ok(valves);
    }

    /**
     * 新增阀门设备
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveValve(@RequestBody ValveDevice valve) {
        boolean success = valveService.saveValve(valve);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (success) {
            result.put("id", valve.getId());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 更新阀门设备
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateValve(
            @PathVariable Long id, 
            @RequestBody ValveDevice valve) {
        valve.setId(id);
        boolean success = valveService.updateValve(valve);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除阀门设备
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteValve(@PathVariable Long id) {
        boolean success = valveService.deleteValve(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 控制阀门开度
     */
    @PostMapping("/{id}/control")
    public ResponseEntity<Map<String, Object>> controlValve(
            @PathVariable Long id, 
            @RequestBody Map<String, Double> request) {
        Double openRatio = request.get("openRatio");
        boolean success = valveService.controlValve(id, openRatio);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("openRatio", openRatio);
        return ResponseEntity.ok(result);
    }
}
