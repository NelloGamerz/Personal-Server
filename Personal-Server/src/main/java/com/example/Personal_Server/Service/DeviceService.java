package com.example.Personal_Server.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Personal_Server.DTO.DeviceOtpVerifyRequest;
import com.example.Personal_Server.DTO.DeviceOtpVerifyResponse;
import com.example.Personal_Server.DTO.DeviceRegisterRequest;
import com.example.Personal_Server.DTO.DeviceRegisterResponse;
import com.example.Personal_Server.DTO.LoginRequest;
import com.example.Personal_Server.DTO.LoginResponse;
import com.example.Personal_Server.Repository.DeviceRepository;
import com.example.Personal_Server.Repository.TokenRepository;
import com.example.Personal_Server.Utils.JwtUtil;
import com.example.Personal_Server.Utils.RedisUtil;
import com.example.Personal_Server.enums.DeviceStatus;
import com.example.Personal_Server.models.Token;
import com.example.Personal_Server.models.TrustedDevices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final RedisUtil redisService;
    private final TotpService totpService;
    private final EncryptionService encryptionService;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public DeviceRegisterResponse register(DeviceRegisterRequest request, String ip) {
        var existing = deviceRepository.findByDeviceId(request.deviceId());

        if (existing.isPresent()) {
            return new DeviceRegisterResponse("Device alreeady exists", existing.get().getStatus().name());
        }

        String secret = totpService.generateSecret();
        String encryptedSecret;
        try {
            encryptedSecret = encryptionService.encrypt(secret);
        } catch (Exception E) {
            log.error("Encryption failed for deviceId={}", request.deviceId(), E);
            throw new RuntimeException("Encryption failed");
        }

        TrustedDevices devices = new TrustedDevices();
        devices.setDeviceId(request.deviceId());
        devices.setDeviceName(request.deviceName());
        devices.setDeviceModel(request.deviceModel());
        devices.setDeviceType(request.deviceType());
        devices.setIpAddress(ip);
        devices.setTotpSecret(encryptedSecret);
        devices.setEmail(request.email());
        devices.setFmcToken(request.fmcToken());
        devices.setStatus(DeviceStatus.PENDING);
        devices.setRegisteredAt(LocalDateTime.now());

        deviceRepository.save(devices);
        redisService.set("device:" + request.deviceId(), DeviceStatus.PENDING.name(), 100, TimeUnit.HOURS);

        String totpUri = totpService.createTotpUri(request.email(), secret, "PersonalServer");

        return new DeviceRegisterResponse(totpUri, DeviceStatus.PENDING.name());
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

    // public String getDeviceStatus(String deviceId) {
    //     String status = null;
    //     try {
    //         status = redisService.get("device:" + deviceId, String.class);
    //         if (status != null) {
    //             return status;
    //         }
    //         status = deviceRepository.getStatus(deviceId);
    //         if (status != null) {
    //             redisService.set(
    //                     "device:" + deviceId,
    //                     status,
    //                     100,
    //                     TimeUnit.HOURS);
    //         }
    //     } catch (Exception ex) {
    //         log.error("Error fetching device status for deviceId={}", deviceId, ex);
    //     }

    //     return status;
    // }

    public String getDeviceStatus(String deviceId) {

    final String cacheKey = "device:" + deviceId;

    try {
        // 1. Try Redis first
        String cachedStatus = redisService.get(cacheKey, String.class);
        if (cachedStatus != null) {
            return cachedStatus;
        }

        // 2. Fetch from Mongo
        DeviceStatus status = deviceRepository.findStatusByDeviceId(deviceId);
        if (status == null) {
            return null;
        }

        // 3. Cache result
        redisService.set(
                cacheKey,
                status.name(),   // enum → String (important)
                100,
                TimeUnit.HOURS
        );

        return status.name();

    } catch (Exception ex) {
        log.error("Error fetching device status for deviceId={}", deviceId, ex);
        return null;
    }
}


    public DeviceOtpVerifyResponse verefyDeviceOtp(DeviceOtpVerifyRequest request) {

        TrustedDevices device = deviceRepository.findByDeviceId(request.deviceId())
                .orElseThrow(() -> new IllegalStateException("Device not found"));

        if(device.getStatus() != DeviceStatus.PENDING){
            return new DeviceOtpVerifyResponse("Device already verified", device.getStatus().name());
        }

        String decryptedSecret;
        try{
            decryptedSecret = encryptionService.decrypt(device.getTotpSecret());
        }
        catch(Exception e){
            log.error("Decryption failed for deviceId={}", request.deviceId(), e);
            throw new RuntimeException("Decryption failed");
        }

        boolean isValid = totpService.verify(decryptedSecret, request.otp());

        if(!isValid){
            throw new IllegalArgumentException("Invalid OTP");
        }

        device.setStatus(DeviceStatus.APPROVED);
        device.setApprovedAt(LocalDateTime.now());
        deviceRepository.save(device);
        redisService.set("device:" + request.deviceId(), DeviceStatus.APPROVED.name(), 100, TimeUnit.HOURS);

        return new DeviceOtpVerifyResponse("Device verified successfully", DeviceStatus.APPROVED.name());
    }

    public LoginResponse Login(LoginRequest request){
        TrustedDevices device = deviceRepository.findByDeviceId(request.deviceId())
                .orElseThrow(() -> new IllegalStateException("Device not found"));
        
        if(device.getStatus() != DeviceStatus.APPROVED){
            return new LoginResponse("Device not approved");
        }

        if(!device.getDeviceFingerprint().equals(request.deviceFingerprint())){
            return new LoginResponse("Device fingerprint mismatch");
        }

        String secret;
        try{
            secret = encryptionService.decrypt(device.getTotpSecret());
        }
        catch(Exception e){
            log.error("Decryption failed for deviceId={}", request.deviceId(), e);
            throw new RuntimeException("Decryption failed");
        }

        if(!totpService.verify(secret, request.otp())){
            return new LoginResponse("Invalid OTP");
        }

        String session = generateAndStoringToken(device.getUserId(), request.deviceId(), request.deviceFingerprint());

        return new LoginResponse(session);
    }

    private String generateAndStoringToken(String userId, String deviceId, String fingerprintHash){
        String accessToken = jwtUtil.generateAccessToken(userId, deviceId, fingerprintHash, true);
        String refreshToken = jwtUtil.generateAccessToken(userId, deviceId, fingerprintHash, true);

        String sessionId = UUID.randomUUID().toString();
        redisService.set("session:" + sessionId, accessToken, 20, TimeUnit.MINUTES);

        Token token = new Token();
        token.setDeviceId(deviceId);
        token.setHashedRefreshToken(refreshToken);
        token.setRevoked(false);
        token.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(token);
        return sessionId;
    }
}
