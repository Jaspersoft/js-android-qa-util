package com.jaspersoft.android.jaspermobile.qa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActionReceiver extends BroadcastReceiver {
    public static final String REMOVE_COOKIES_ACTION = "jaspermobile.internal.action.REMOVE_COOKIES";
    public static final String REMOVE_ALL_ACCOUNTS = "jaspermobile.internal.action.REMOVE_ALL_ACCOUNTS";

    public ActionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (REMOVE_COOKIES_ACTION.equals(action)) {
            UtilEvent.get(context).fireRemoveCookiesEvent();
        } else if (REMOVE_ALL_ACCOUNTS.equals(action)) {
            UtilEvent.get(context).fireRemoveAccountsEvent();
        }
    }
}
