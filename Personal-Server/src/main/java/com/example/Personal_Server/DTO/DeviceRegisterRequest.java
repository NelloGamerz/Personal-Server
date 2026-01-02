package com.example.Personal_Server.DTO;

public record DeviceRegisterRequest(
        String deviceId,
        String deviceName,
        String deviceModel,
        String deviceType,
        String deviceFingerprint,
        String fmcToken,
        String email) {
}