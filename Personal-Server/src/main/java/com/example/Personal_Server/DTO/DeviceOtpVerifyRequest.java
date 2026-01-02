package com.example.Personal_Server.DTO;

public record DeviceOtpVerifyRequest(
        String deviceId,
        String otp) {
}
