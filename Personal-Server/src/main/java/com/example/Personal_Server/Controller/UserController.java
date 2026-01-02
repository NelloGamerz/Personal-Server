package com.example.Personal_Server.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Personal_Server.DTO.DeviceOtpVerifyRequest;
import com.example.Personal_Server.DTO.DeviceOtpVerifyResponse;
import com.example.Personal_Server.DTO.DeviceRegisterRequest;
import com.example.Personal_Server.DTO.DeviceRegisterResponse;
import com.example.Personal_Server.DTO.LoginRequest;
import com.example.Personal_Server.DTO.LoginResponse;
import com.example.Personal_Server.Service.DeviceService;
import com.example.Personal_Server.Utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final DeviceService deviceService;
    private final CookieUtil cookieUtil;
    @PostMapping("/register-device")
    public ResponseEntity<DeviceRegisterResponse> register(
            @RequestBody DeviceRegisterRequest request,
            HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        DeviceRegisterResponse response = deviceService.register(request, ip);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-device-otp")
    public ResponseEntity<DeviceOtpVerifyResponse> verefyOtp(
        @RequestBody DeviceOtpVerifyRequest request
    ){
        DeviceOtpVerifyResponse response = deviceService.verefyDeviceOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> ogin(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ){
        LoginResponse result = deviceService.Login(request);

        cookieUtil.addCookie(response, "sessionId", result.response(), 100);
        return ResponseEntity.ok("Login successful");
    }
}
