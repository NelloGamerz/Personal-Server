package com.example.Personal_Server.DTO;

public record LoginRequest( 
    String password,
    String signedpayload,
    String signature
) {}