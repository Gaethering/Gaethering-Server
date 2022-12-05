package com.gaethering.gaetheringserver.util;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setDataExpire(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(duration));
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

}
