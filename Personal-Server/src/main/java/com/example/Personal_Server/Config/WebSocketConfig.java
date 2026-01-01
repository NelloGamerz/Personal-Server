package com.example.Personal_Server.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.Personal_Server.Filters.DeviceValidationHandshakeInterceptor;
import com.example.Personal_Server.Filters.WebSocketAuthFilter;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    private final DeviceValidationHandshakeInterceptor DeviceValidationHandshakeInterceptor;
    private final WebSocketAuthFilter WebSocketAuthFilter;
    public WebSocketConfig(DeviceValidationHandshakeInterceptor deviceValidationHandshakeInterceptor, WebSocketAuthFilter webSocketAuthFilter) {
        DeviceValidationHandshakeInterceptor = deviceValidationHandshakeInterceptor;
        WebSocketAuthFilter = webSocketAuthFilter;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(null, "/we/control")
                .addInterceptors(DeviceValidationHandshakeInterceptor, WebSocketAuthFilter)
                .setAllowedOrigins("*");
    }
}
