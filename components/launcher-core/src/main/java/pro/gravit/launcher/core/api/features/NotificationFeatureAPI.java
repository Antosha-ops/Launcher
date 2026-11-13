package pro.gravit.launcher.core.api.features;

public interface NotificationFeatureAPI extends FeatureAPI {
    void addListener(NotificationListener listener);

    void removeListener(NotificationListener listener);

    interface NotificationListener {
        void onNotification(Notification notification);
    }

    record Notification(String header, String message, NotificationType type) {

    }

    enum NotificationType {
        INFO, WARN, ERROR, OTHER
    }
}
