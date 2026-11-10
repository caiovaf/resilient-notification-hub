package com.caiovaf.notification.dto;

import com.caiovaf.notification.model.NotificationChannel;
import java.time.Instant;

public record NotificationResponse(
    String notificationId,
    String tenantId,
    NotificationChannel channel,
    String recipient,
    String status,
    String providerUsed,
    boolean fallbackTriggered,
    Instant dispatchedAt
) {}
