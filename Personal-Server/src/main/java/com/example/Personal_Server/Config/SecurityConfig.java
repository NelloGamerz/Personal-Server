package com.example.Personal_Server.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Personal_Server.Filters.DeviceValidationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

//     private final DeviceValidationFilter deviceValidationFilter;

//     public SecurityConfig() {
//         // this.deviceValidationFilter = deviceValidationFilter;
//     }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // ❌ Disable defaults
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // ✅ Stateless JWT-based security
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ Public & protected routes
                // .authorizeHttpRequests(auth -> auth
                //         .requestMatchers(
                //                 "/api/files/**",
                //                 "/auth/login",
                //                 "/auth/register",
                //                 "/auth/forgot-password",
                //                 "/auth/reset-password",
                //                 "/reset-password/**",
                //                 "/auth/check-username",
                //                 "/memes/memepage/**",
                //                 "/health/check",
                //                 "/ws/**"
                //         ).permitAll()
                //         .anyRequest().authenticated()
                // )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())


                // ✅ Custom unauthorized response
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                    "{\"error\":\"Unauthorized. Please log in again.\"}"
                            );
                        })
                )

                // ✅ JWT first, then rate limit
                // .addFilterBefore(deviceValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
