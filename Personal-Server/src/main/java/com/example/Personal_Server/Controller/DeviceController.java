package com.example.Personal_Server.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Personal_Server.Service.DeviceService;
import com.example.Personal_Server.enums.DeviceStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping("/update-status")
    public ResponseEntity<String> updateStatus(
            @RequestBody String deviceId,
            @RequestBody DeviceStatus status) {
        deviceService.updateDeviceStatus(deviceId, status);
        return ResponseEntity.ok("Device status updated successfully");
    }

    @GetMapping("/status/{deviceId}")
    public ResponseEntity<String> getDeviceStatus(@RequestParam String deviceId){
        String status = deviceService.getDeviceStatus(deviceId);
        return ResponseEntity.ok(status);
    }
}
