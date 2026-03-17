package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.Boiler;
import com.boiler.service.BoilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 锅炉设备控制器
 * 处理锅炉设备相关的HTTP请求，提供RESTful API接口
 */
@RestController
@RequestMapping("/api/boiler")
@RequiredArgsConstructor
@CrossOrigin
public class BoilerController {

    private final BoilerService boilerService;

    /**
     * 分页获取锅炉列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listBoilers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String boilerName) {
        
        Page<Boiler> pageResult = boilerService.listBoilers(page, size, boilerName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取锅炉
     */
    @GetMapping("/{id}")
    public ResponseEntity<Boiler> getBoilerById(@PathVariable Long id) {
        Boiler boiler = boilerService.getBoilerById(id);
        return ResponseEntity.ok(boiler);
    }

    /**
     * 获取所有锅炉
     */
    @GetMapping("/all")
    public ResponseEntity<List<Boiler>> listAllBoilers() {
        List<Boiler> boilers = boilerService.listAllBoilers();
        return ResponseEntity.ok(boilers);
    }

    /**
     * 新增锅炉
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveBoiler(@RequestBody Boiler boiler) {
        boolean success = boilerService.saveBoiler(boiler);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (success) {
            result.put("id", boiler.getId());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 更新锅炉
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBoiler(
            @PathVariable Long id, 
            @RequestBody Boiler boiler) {
        boiler.setId(id);
        boolean success = boilerService.updateBoiler(boiler);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除锅炉
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBoiler(@PathVariable Long id) {
        boolean success = boilerService.deleteBoiler(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    /**
     * 控制锅炉
     */
    @PostMapping("/{id}/control")
    public ResponseEntity<Map<String, Object>> controlBoiler(
            @PathVariable Long id, 
            @RequestBody Map<String, String> command) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "控制命令已发送");
        return ResponseEntity.ok(result);
    }
}
