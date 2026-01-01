package com.example.Personal_Server.Filters;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Personal_Server.Service.DeviceValidationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceValidationFilter extends OncePerRequestFilter {

    private final DeviceValidationService deviceValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{
        String deviceId = request.getHeader("X-DEVICE-ID");
        if(!deviceValidationService.validateDevice(deviceId)){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Device");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
