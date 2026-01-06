package com.example.Personal_Server.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.Personal_Server.enums.DeviceStatus;
import com.example.Personal_Server.models.TrustedDevices;

public interface DeviceRepository extends MongoRepository<TrustedDevices, String>{
    Optional<TrustedDevices> findByDeviceId(String deviceId);
    // Optional<TrustedDevices> findStatusByDeviceId(String deviceId);

    @Query(value = "{ 'deviceId': ?0 }", fields = "{ 'status': 1 }")
DeviceStatus findStatusByDeviceId(String deviceId);

}
