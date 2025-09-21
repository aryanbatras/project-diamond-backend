package com.backend.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Project Diamond Backend");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");
        format.setTimeZone(TimeZone.getTimeZone("UTC+5:30"));
        String utc = format.format(new Date(System.currentTimeMillis()));
        response.put("timestamp", utc);
        return ResponseEntity.ok(response);
    }
}
