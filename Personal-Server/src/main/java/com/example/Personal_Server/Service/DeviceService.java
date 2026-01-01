package com.example.Personal_Server.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Personal_Server.DTO.DeviceRegisterRequest;
import com.example.Personal_Server.DTO.DeviceRegisterResponse;
import com.example.Personal_Server.Repository.DeviceRepository;
import com.example.Personal_Server.Utils.RedisUtil;
import com.example.Personal_Server.enums.DeviceStatus;
import com.example.Personal_Server.models.TrustedDevices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final RedisUtil redisService;

    public DeviceRegisterResponse register(DeviceRegisterRequest request, String ip) {
        var existing = deviceRepository.findByDeviceId(request.deviceId());

        if (existing.isPresent()) {
            return new DeviceRegisterResponse("Device alreeady exists", existing.get().getStatus().name());
        }

        TrustedDevices devices = new TrustedDevices();
        devices.setDeviceId(request.deviceId());
        devices.setDeviceName(request.deviceName());
        devices.setDeviceModel(request.deviceModel());
        devices.setDeviceType(request.deviceType());
        devices.setIpAddress(ip);
        devices.setStatus(DeviceStatus.PENDING);
        devices.setRegisteredAt(LocalDateTime.now());

        deviceRepository.save(devices);
        redisService.set("device:" + request.deviceId(), DeviceStatus.PENDING.name(), 100, TimeUnit.HOURS);

        return new DeviceRegisterResponse("Registration request Submitted", "PENDING");
    }

    @Transactional
    public void updateDeviceStatus(String deviceId, DeviceStatus status) {

        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("DeviceId must not be null or empty");
        }

        if (status == null) {
            throw new IllegalArgumentException("DeviceStatus must not be null");
        }

        TrustedDevices device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found with deviceId: " + deviceId));

        // Idempotency check (avoid unnecessary writes)
        if (status == device.getStatus()) {
            return;
        }

        device.setStatus(status);

        try {
            deviceRepository.save(device);
        } catch (Exception ex) {
            log.error("Failed to update device status in DB for deviceId={}", deviceId, ex);
            throw ex;
        }

        // Redis update should not rollback DB
        try {
            redisService.set(
                    "device:" + deviceId,
                    status.name(),
                    100,
                    TimeUnit.HOURS);
        } catch (Exception ex) {
            log.warn(
                    "Redis update failed for deviceId={}, DB update succeeded",
                    deviceId,
                    ex);
        }
    }

    public String getDeviceStatus(String deviceId) {
        String status = null;
        try {
            status = redisService.get("device:" + deviceId, String.class);
            if (status != null) {
                return status;
            }
            status = deviceRepository.getStatus(deviceId);
            if (status != null) {
                redisService.set(
                        "device:" + deviceId,
                        status,
                        100,
                        TimeUnit.HOURS);
            }
        } catch (Exception ex) {
            log.error("Error fetching device status for deviceId={}", deviceId, ex);
        }

        return status;
    }
}
