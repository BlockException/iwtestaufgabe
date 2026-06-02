package net.iwmedia.report.common.service;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void savePendingNotification(UUID recipient, String message);
    List<String> fetchPendingNotifications(UUID recipient);
    void removeNotifications(UUID recipient);
}
