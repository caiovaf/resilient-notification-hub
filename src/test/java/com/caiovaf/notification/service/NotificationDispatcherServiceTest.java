package com.caiovaf.notification.service;

import com.caiovaf.notification.dto.DispatchNotificationRequest;
import com.caiovaf.notification.dto.NotificationResponse;
import com.caiovaf.notification.model.NotificationChannel;
import com.caiovaf.notification.model.NotificationPriority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDispatcherServiceTest {

    private final NotificationDispatcherService dispatcherService = new NotificationDispatcherService();

    @Test
    @DisplayName("Should successfully dispatch via primary provider under healthy conditions")
    void shouldDispatchViaPrimaryProvider() {
        DispatchNotificationRequest request = new DispatchNotificationRequest(
            "tenant_alpha",
            NotificationChannel.EMAIL,
            "test@enterprise.com",
            "Billing update",
            "Your invoice is ready",
            NotificationPriority.NORMAL
        );

        NotificationResponse response = dispatcherService.dispatch(request);

        assertNotNull(response);
        assertEquals("DELIVERED", response.status());
        assertEquals("PRIMARY_PROVIDER_AWS_SES", response.providerUsed());
        assertFalse(response.fallbackTriggered());
    }

    @Test
    @DisplayName("Should trigger secondary backup provider when primary circuit trips or fails")
    void shouldTriggerFallbackProvider() {
        DispatchNotificationRequest request = new DispatchNotificationRequest(
            "tenant_alpha",
            NotificationChannel.EMAIL,
            "test@enterprise.com",
            "Billing update",
            "Your invoice is ready",
            NotificationPriority.HIGH
        );

        NotificationResponse response = dispatcherService.fallbackDispatch(request, new RuntimeException("Connection timed out"));

        assertNotNull(response);
        assertEquals("DELIVERED_VIA_FALLBACK", response.status());
        assertEquals("SECONDARY_BACKUP_PROVIDER", response.providerUsed());
        assertTrue(response.fallbackTriggered());
    }
}
