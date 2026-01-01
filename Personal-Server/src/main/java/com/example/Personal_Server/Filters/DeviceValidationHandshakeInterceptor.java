package com.example.Personal_Server.Filters;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.Personal_Server.Service.DeviceValidationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceValidationHandshakeInterceptor implements HandshakeInterceptor {

    private final DeviceValidationService deviceValidationService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wshandler,
            Map<String, Object> attributes) {
        var header = request.getHeaders();
        String deviceId = header.getFirst("X-DEVICE-ID");

        if(!deviceValidationService.validateDevice(deviceId)){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        attributes.put("DEVICE_ID", deviceId);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wshandler,
            Exception exception) {
    }
}
