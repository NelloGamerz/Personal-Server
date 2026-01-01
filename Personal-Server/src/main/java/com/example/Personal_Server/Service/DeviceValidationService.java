package com.example.Personal_Server.Service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.example.Personal_Server.Repository.DeviceRepository;
import com.example.Personal_Server.Utils.RedisUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceValidationService {
    
    private final RedisUtil redisUtil;
    private final DeviceRepository deviceRepository;

    public boolean validateDevice(String deviceId){
        if(deviceId == null || deviceId.isBlank()){
            return false;
        }

        String status = redisUtil.get("device:" + deviceId, String.class);
        if(status == null){
            return deviceRepository.findByDeviceId(deviceId)
                .map(device -> {
                    String deviceStatus = device.getStatus().name();
                    redisUtil.set("device:" + deviceId, deviceStatus, 100, TimeUnit.HOURS);
                    return "APPROVED".equals(deviceStatus);
                }).orElse(false);
        }

        return "APPROVED".equals(status);
    }
}
