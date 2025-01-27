package com.simplecity.amp_library.playback;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.os.Build;
import android.support.annotation.Nullable;
import com.simplecity.amp_library.R;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;

class DummyNotificationHelper {

    private static int notification_id_dummy = 5;

    private boolean isShowingDummyNotification;
    private boolean isForegroundedByApp = false;

    private static String channel_id = "channel_dummy";

    // Must be greater than 10000
    // See https://github.com/aosp-mirror/platform_frameworks_base/blob/e80b45506501815061b079dcb10bf87443bd385d/services/core/java/com/android/server/am/ActiveServices.java
    // (SERVICE_START_FOREGROUND_TIMEOUT = 10*1000)
    //
    private static int notification_stop_delay = 12500;

    @Nullable
    private Disposable dummyNotificationDisposable = null;

    void setForegroundedByApp(boolean foregroundedByApp) {
        isForegroundedByApp = foregroundedByApp;
    }

    void showDummyNotification(Service service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isShowingDummyNotification) {
                NotificationManager notificationManager = service.getSystemService(NotificationManager.class);
                NotificationChannel channel = notificationManager.getNotificationChannel(channel_id);
                if (channel == null) {
                    channel = new NotificationChannel(channel_id, service.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                    channel.enableLights(false);
                    channel.enableVibration(false);
                    channel.setSound(null, null);
                    channel.setShowBadge(false);
                    channel.setImportance(NotificationManager.IMPORTANCE_LOW);
                    notificationManager.createNotificationChannel(channel);
                }

                Notification notification = new Notification.Builder(service, channel_id)
                        .setContentTitle(service.getString(R.string.app_name))
                        .setContentText(service.getString(R.string.notification_text_shuttle_running))
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .build();

                notificationManager.notify(notification_id_dummy, notification);

                if (!isForegroundedByApp) {
                    service.startForeground(notification_id_dummy, notification);
                }

                isShowingDummyNotification = true;
            }
        }

        if (dummyNotificationDisposable != null) {
            dummyNotificationDisposable.dispose();
        }
        dummyNotificationDisposable = Completable.timer(notification_stop_delay, TimeUnit.MILLISECONDS).doOnComplete(() -> removeDummyNotification(service)).subscribe();
    }

    void teardown(Service service) {

        removeDummyNotification(service);

        if (dummyNotificationDisposable != null) {
            dummyNotificationDisposable.dispose();
        }
    }

    private void removeDummyNotification(Service service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isShowingDummyNotification) {

                if (dummyNotificationDisposable != null) {
                    dummyNotificationDisposable.dispose();
                }

                if (!isForegroundedByApp) {
                    service.stopForeground(true);
                }

                NotificationManager notificationManager = service.getSystemService(NotificationManager.class);
                notificationManager.cancel(notification_id_dummy);

                isShowingDummyNotification = false;
            }
        }
    }
}