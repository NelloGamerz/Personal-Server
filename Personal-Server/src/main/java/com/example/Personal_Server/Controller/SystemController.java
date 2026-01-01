package com.example.Personal_Server.Controller;

import java.lang.management.ManagementFactory;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class SystemController {
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus(){
        Map<String, Object> result = Map.of(
            "os", System.getProperty("os.name"),
            "osVersion", System.getProperty("os.version"),
            "javaVersion", System.getProperty("java.version"),
            "availableProcessors", Runtime.getRuntime().availableProcessors(),
            "freeMemoryMB", Runtime.getRuntime().freeMemory() / (1024 * 1024),
            "totalMemoryMB", Runtime.getRuntime().totalMemory() / (1024 * 1024),
            "uptime", ManagementFactory.getRuntimeMXBean().getUptime()
        );

        return ResponseEntity.ok(result);
    }
}
