package com.example.Personal_Server.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Personal_Server.DTO.DeviceRegisterRequest;
import com.example.Personal_Server.DTO.DeviceRegisterResponse;
import com.example.Personal_Server.Service.DeviceService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final DeviceService deviceService;
    @PostMapping("/register")
    public ResponseEntity<DeviceRegisterResponse> register(
            @RequestBody DeviceRegisterRequest request,
            HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        DeviceRegisterResponse response = deviceService.register(request, ip);
        return ResponseEntity.ok(response);
    }

    // @PostMapping("/login")
    // public ResponseEntity<String> login(
    //         @RequestBody String password,
    // ){

    // }
}
