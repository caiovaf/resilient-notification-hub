package com.caiovaf.notification.dto;

import com.caiovaf.notification.model.NotificationChannel;
import com.caiovaf.notification.model.NotificationPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DispatchNotificationRequest(
    @NotBlank(message = "Tenant / User ID is required")
    String tenantId,

    @NotNull(message = "Channel is required")
    NotificationChannel channel,

    @NotBlank(message = "Recipient address/phone/endpoint is required")
    String recipient,

    @NotBlank(message = "Message subject is required")
    String subject,

    @NotBlank(message = "Message payload body is required")
    String body,

    NotificationPriority priority
) {}
