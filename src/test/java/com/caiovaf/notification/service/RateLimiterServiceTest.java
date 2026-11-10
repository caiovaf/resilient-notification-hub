package com.caiovaf.notification.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RateLimiterService rateLimiterService;

    @Test
    @DisplayName("Should allow request when counter is below maximum threshold")
    void shouldAllowWhenBelowThreshold() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("ratelimit:notification:tenant_1")).thenReturn(15L);

        boolean allowed = rateLimiterService.isAllowed("tenant_1");

        assertTrue(allowed);
    }

    @Test
    @DisplayName("Should reject request when counter exceeds 60 requests per minute")
    void shouldRejectWhenExceedingThreshold() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("ratelimit:notification:tenant_heavy")).thenReturn(61L);

        boolean allowed = rateLimiterService.isAllowed("tenant_heavy");

        assertFalse(allowed);
    }

    @Test
    @DisplayName("Should set 1-minute TTL on first request")
    void shouldSetTtlOnFirstRequest() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("ratelimit:notification:tenant_new")).thenReturn(1L);

        boolean allowed = rateLimiterService.isAllowed("tenant_new");

        assertTrue(allowed);
        verify(redisTemplate).expire(eq("ratelimit:notification:tenant_new"), any(Duration.class));
    }
}
