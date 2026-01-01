package com.example.Personal_Server.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Personal_Server.models.TrustedDevices;

public interface DeviceRepository extends MongoRepository<TrustedDevices, String>{
    Optional<TrustedDevices> findByDeviceId(String deviceId);
    String getStatus(String deviceId);
}
