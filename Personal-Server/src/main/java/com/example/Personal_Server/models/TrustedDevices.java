package com.example.Personal_Server.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.Personal_Server.enums.DeviceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "devices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrustedDevices {
    @Id
    private String id;

    private String deviceId;            
    private String deviceName;          
    private String deviceModel;         
    private String deviceType;          
    private String deviceFingerprint;   
    private String ipAddress;           
    private String publicKey;
    private String totpSecret;          

    private DeviceStatus status;        

    private LocalDateTime registeredAt;
    private LocalDateTime approvedAt;
    private LocalDateTime lastActive;
}
