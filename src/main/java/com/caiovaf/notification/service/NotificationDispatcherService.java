package com.caiovaf.notification.service;

import com.caiovaf.notification.dto.DispatchNotificationRequest;
import com.caiovaf.notification.dto.NotificationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class NotificationDispatcherService {

    @CircuitBreaker(name = "notificationProvider", fallbackMethod = "fallbackDispatch")
    public NotificationResponse dispatch(DispatchNotificationRequest request) {
        String notificationId = "ntf_" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Attempting primary provider dispatch [id={}]: channel={}, recipient={}",
            notificationId, request.channel(), request.recipient());

        // Primary provider logic: Simulating upstream dispatch
        return new NotificationResponse(
            notificationId,
            request.tenantId(),
            request.channel(),
            request.recipient(),
            "DELIVERED",
            "PRIMARY_PROVIDER_AWS_SES",
            false,
            Instant.now()
        );
    }

    public NotificationResponse fallbackDispatch(DispatchNotificationRequest request, Throwable ex) {
        String notificationId = "ntf_fb_" + UUID.randomUUID().toString().substring(0, 8);
        log.warn("PRIMARY PROVIDER FAILED (Circuit breaker open/degraded: {}). Engaging secondary fallback provider for recipient={}",
            ex.getMessage(), request.recipient());

        // Secondary fallback provider (e.g., Mailgun / SendGrid / Twilio backup)
        return new NotificationResponse(
            notificationId,
            request.tenantId(),
            request.channel(),
            request.recipient(),
            "DELIVERED_VIA_FALLBACK",
            "SECONDARY_BACKUP_PROVIDER",
            true,
            Instant.now()
        );
    }
}
