package com.boiler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boiler.entity.HeatUser;
import com.boiler.service.HeatUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/heat-user")
@RequiredArgsConstructor
@CrossOrigin
public class HeatUserController {

    private final HeatUserService heatUserService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String userName) {
        
        Page<HeatUser> pageResult = heatUserService.listUsers(page, size, buildingId, userName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeatUser> getUserById(@PathVariable Long id) {
        HeatUser user = heatUserService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<HeatUser>> listUsersByBuildingId(@PathVariable Long buildingId) {
        List<HeatUser> users = heatUserService.listUsersByBuildingId(buildingId);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody HeatUser user) {
        boolean success = heatUserService.saveUser(user);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (success) {
            result.put("id", user.getId());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id, 
            @RequestBody HeatUser user) {
        user.setId(id);
        boolean success = heatUserService.updateUser(user);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        boolean success = heatUserService.deleteUser(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
}
