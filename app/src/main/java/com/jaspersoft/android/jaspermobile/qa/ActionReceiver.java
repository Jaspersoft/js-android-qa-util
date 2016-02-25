package com.jaspersoft.android.jaspermobile.qa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActionReceiver extends BroadcastReceiver {
    public static final String REMOVE_COOKIES_ACTION = "jaspermobile.internal.action.REMOVE_COOKIES";
    public static final String REMOVE_ALL_ACCOUNTS = "jaspermobile.internal.action.REMOVE_ALL_ACCOUNTS";
    public static final String INVALIDATE_PASSWORD = "jaspermobile.internal.action.INVALIDATE_PASSWORD";

    public ActionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UtilEvent utilEvent = UtilEvent.get(context);

        if (REMOVE_COOKIES_ACTION.equals(action)) {
            utilEvent.fireRemoveCookiesEvent();
        } else if (INVALIDATE_PASSWORD.equals(action)) {
            utilEvent.fireInvalidatePasswordEvent();
        } else if (REMOVE_ALL_ACCOUNTS.equals(action)) {
            utilEvent.fireRemoveAccountsEvent();
        }
    }
}
