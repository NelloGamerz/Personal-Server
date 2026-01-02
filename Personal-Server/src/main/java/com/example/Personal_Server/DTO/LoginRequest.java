package com.example.Personal_Server.DTO;

public record LoginRequest( 
    String deviceId,
    String otp,
    String deviceFingerprint
) {}