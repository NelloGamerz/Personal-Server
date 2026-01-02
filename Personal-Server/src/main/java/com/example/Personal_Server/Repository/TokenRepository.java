package com.example.Personal_Server.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Personal_Server.models.Token;

public interface TokenRepository extends MongoRepository<Token, String>{
    Optional<Token> findByDeviceId(String deviceId);
    
}
