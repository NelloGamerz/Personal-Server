package com.example.Personal_Server.models;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogs {
    @Id
    private String id;
    private String userId;
    private String deviceId;
    private String action;
    private String ip;
    private String userAgent;
    private boolean success;
    private String reason;
    private Instant timestamp;
}
