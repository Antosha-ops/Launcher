package pro.gravit.launcher.base.request;

import pro.gravit.launcher.base.events.NotificationEvent;
import pro.gravit.launcher.base.request.auth.GetAvailabilityAuthRequest;
import pro.gravit.launcher.base.request.update.LauncherRequest;
import pro.gravit.launcher.core.api.features.CoreFeatureAPI;
import pro.gravit.launcher.core.api.features.NotificationFeatureAPI;
import pro.gravit.launcher.core.api.method.AuthMethod;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestCoreFeatureAPIImpl implements CoreFeatureAPI, NotificationFeatureAPI {
    private final RequestService request;
    private final List<NotificationListener> notificationListeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean notificationHandlerRegistered = new AtomicBoolean();

    public RequestCoreFeatureAPIImpl(RequestService request) {
        this.request = request;
    }



    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public CompletableFuture<List<AuthMethod>> getAuthMethods() {
        return request.request(new GetAvailabilityAuthRequest()).thenApply(response -> (List) response.list);
    }

    @Override
    public CompletableFuture<LauncherUpdateInfo> checkUpdates() {
        return request.request(new LauncherRequest()).thenApply(response -> new LauncherUpdateInfo(response.url,
                "Unknown", response.needUpdate, response.needUpdate, response.updateCheckIntervalMinutes));
    }

    @Override
    public void addListener(NotificationListener listener) {
        notificationListeners.add(listener);
        if (notificationHandlerRegistered.compareAndSet(false, true)) {
            request.registerEventHandler(new NotificationEventHandler());
        }
    }

    @Override
    public void removeListener(NotificationListener listener) {
        notificationListeners.remove(listener);
    }

    private static NotificationType toNotificationType(NotificationEvent.NotificationType icon) {
        if (icon == null) {
            return NotificationType.INFO;
        }
        return switch (icon) {
            case INFO -> NotificationType.INFO;
            case WARN -> NotificationType.WARN;
            case ERROR -> NotificationType.ERROR;
            case OTHER -> NotificationType.OTHER;
        };
    }

    private class NotificationEventHandler implements RequestService.EventHandler {
        @Override
        public <T extends WebSocketEvent> boolean eventHandle(T event) {
            if (event instanceof NotificationEvent notificationEvent) {
                Notification notification = new Notification(notificationEvent.head, notificationEvent.message,
                        toNotificationType(notificationEvent.icon));
                for (NotificationListener listener : notificationListeners) {
                    listener.onNotification(notification);
                }
            }
            return false;
        }
    }
}
