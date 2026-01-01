package com.example.Personal_Server.Utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // @Autowired
    // private ObjectMapper objectMapper;

    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(o.toString(), entityClass);
        } catch (Exception e) {
            log.error("Exception " + e);
            return null;
        }
    }

    public void set(String key, Object o, long time, TimeUnit unit) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonValue, unit.toSeconds(time), TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Exception while saving to Redis: " + e.getMessage());
        }
    }
}
