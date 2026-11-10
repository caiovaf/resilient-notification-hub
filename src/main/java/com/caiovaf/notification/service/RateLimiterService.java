package com.caiovaf.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private static final String RATE_LIMIT_PREFIX = "ratelimit:notification:";
    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    public boolean isAllowed(String tenantId) {
        String key = RATE_LIMIT_PREFIX + tenantId;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        if (count != null && count > MAX_REQUESTS_PER_MINUTE) {
            log.warn("Rate limit exceeded for tenant [{}]: currentCount={}", tenantId, count);
            return false;
        }

        return true;
    }
}
