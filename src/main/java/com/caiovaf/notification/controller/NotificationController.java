package com.caiovaf.notification.controller;

import com.caiovaf.notification.dto.DispatchNotificationRequest;
import com.caiovaf.notification.dto.NotificationResponse;
import com.caiovaf.notification.service.NotificationDispatcherService;
import com.caiovaf.notification.service.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Hub API", description = "Resilient multi-channel notification dispatching with rate limits and circuit breakers")
public class NotificationController {

    private final NotificationDispatcherService dispatcherService;
    private final RateLimiterService rateLimiterService;

    @PostMapping("/dispatch")
    @Operation(summary = "Dispatch a notification across Email, SMS, Push, or Webhook")
    public ResponseEntity<?> dispatchNotification(@Valid @RequestBody DispatchNotificationRequest request) {
        log.info("Incoming notification request for tenantId={}: channel={}, recipient={}",
            request.tenantId(), request.channel(), request.recipient());

        // Check Redis Rate Limit
        if (!rateLimiterService.isAllowed(request.tenantId())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate limit exceeded for tenant " + request.tenantId() + ". Maximum 60 requests per minute allowed.");
        }

        NotificationResponse response = dispatcherService.dispatch(request);
        return ResponseEntity.ok(response);
    }
}
