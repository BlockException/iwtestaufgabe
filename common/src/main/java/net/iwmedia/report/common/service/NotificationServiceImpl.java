package net.iwmedia.report.common.service;

import dev.morphia.Datastore;
import net.iwmedia.report.common.model.NotificationModel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationServiceImpl implements NotificationService {
    private final MongoService mongoService;

    public NotificationServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void savePendingNotification(UUID recipient, String message) {
        Datastore datastore = mongoService.getDatastore();
        NotificationModel model = new NotificationModel();
        model.setRecipient(recipient);
        model.setMessage(message);
        model.setCreatedAt(Instant.now());
        datastore.save(model);
    }

    @Override
    public List<String> fetchPendingNotifications(UUID recipient) {
        Datastore datastore = mongoService.getDatastore();
        List<NotificationModel> notifications = datastore.find(NotificationModel.class)
                .filter("recipient", recipient)
                .iterator()
                .toList();
        return notifications.stream()
                .map(NotificationModel::getMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void removeNotifications(UUID recipient) {
        Datastore datastore = mongoService.getDatastore();
        datastore.find(NotificationModel.class)
                .filter("recipient", recipient)
                .delete();
    }
}
