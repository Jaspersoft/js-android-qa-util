package com.jaspersoft.android.jaspermobile.qa;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class ControlService extends Service {
    private static final int CONSTANT_NOTIFICATION_ID = 100;
    private static final int START_ACTIVITY = 10;
    private static final int FLUSH_COOKIES = 100;
    private static final int REMOVE_ACCOUNTS = 200;
    private static final int INVALIDATE_PASSWORD = 300;

    public static void start(Context context) {
        Intent startIntent = new Intent(context, ControlService.class);
        context.startService(startIntent);
    }

    public ControlService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundNotification();
    }

    private void startForegroundNotification() {
        Intent startPage = new Intent(this, MainActivity.class);
        startPage.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent startActivity = PendingIntent.getActivity(this, START_ACTIVITY, startPage, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Jasper QA Util")
                .setContentText("Stub")
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Remove cookie", createRemoveCookiesAction())
                .addAction(android.R.drawable.ic_menu_agenda, "Remove accounts", createRemoveAccountsAction())
                .addAction(android.R.drawable.ic_menu_edit, "Invalidate password", createInvalidatePasswordAction())
                .setContentIntent(startActivity)
                .build();
        startForeground(CONSTANT_NOTIFICATION_ID, notification);
    }

    private PendingIntent createRemoveAccountsAction() {
        Intent removeAccountsIntent = new Intent(ActionReceiver.REMOVE_ALL_ACCOUNTS);
        return PendingIntent.getBroadcast(this, REMOVE_ACCOUNTS, removeAccountsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createRemoveCookiesAction() {
        Intent intent = new Intent(ActionReceiver.REMOVE_COOKIES_ACTION);
        return PendingIntent.getBroadcast(this, FLUSH_COOKIES, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createInvalidatePasswordAction() {
        Intent intent = new Intent(ActionReceiver.INVALIDATE_PASSWORD);
        return PendingIntent.getBroadcast(this, INVALIDATE_PASSWORD, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
