package com.example.Personal_Server.Filters;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.Personal_Server.Utils.CookieUtil;
import com.example.Personal_Server.Utils.JwtUtil;
import com.example.Personal_Server.Utils.RedisUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class WebSocketAuthFilter implements HandshakeInterceptor {

    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    public WebSocketAuthFilter(RedisUtil redisUtil, CookieUtil cookieUtil, JwtUtil jwtUtil) {
        this.redisUtil = redisUtil;
        this.cookieUtil = cookieUtil;
        this.jwtUtil = jwtUtil;
    }
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wshandler,
            Map<String, Object> attributes) throws Exception {
        if (!(request instanceof ServletServerHttpRequest))
            return true;

        HttpHeaders headers = request.getHeaders();
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        Cookie[] cookies = servletRequest.getCookies();

        String sessionId = null;
        String deviceId = null;
        deviceId = headers.getFirst("X-Device-ID");

        for (Cookie cookie : cookies) {
            if ("SESSIONID".equals(cookie.getName())) {
                sessionId = cookie.getValue();
            }
        }

        if (deviceId == null || deviceId.isEmpty() || sessionId == null || sessionId.isEmpty()) {
            System.out.println("❌ Missing Device ID or Session ID");
            return false;
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wshandler,
            Exception exception) {
    }
}
