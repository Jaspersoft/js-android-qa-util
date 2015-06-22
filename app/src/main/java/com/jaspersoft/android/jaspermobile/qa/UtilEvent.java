/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.qa;

import android.content.Context;
import android.content.Intent;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class UtilEvent {
    private static final String REMOVE_COOKIES = "jaspermobile.util.action.REMOVE_COOKIES";
    private static final String DEPRECATE_COOKIES = "jaspermobile.util.action.DEPRECATE_COOKIES";
    private static final String REMOVE_ALL_ACCOUNTS = "jaspermobile.util.action.REMOVE_ALL_ACCOUNTS";
    private static final String DOWNGRADE_SERVER_VERSION = "jaspermobile.util.action.DOWNGRADE_SERVER_VERSION";
    private static final String CHANGE_SERVER_EDITION = "jaspermobile.util.action.CHANGE_SERVER_EDITION";

    private final Context mContext;

    private UtilEvent(Context context) {
        mContext = context;
    }

    public static UtilEvent get(Context context) {
        return new UtilEvent(context);
    }

    public void fireRemoveCookiesEvent() {
        Intent intent = new Intent(REMOVE_COOKIES);
        mContext.sendBroadcast(intent);
    }

    public void fireDeprecatedCookiesEvent() {
        Intent intent = new Intent(DEPRECATE_COOKIES);
        mContext.sendBroadcast(intent);
    }

    public void fireRemoveAccountsEvent() {
        Intent intent = new Intent(REMOVE_ALL_ACCOUNTS);
        mContext.sendBroadcast(intent);
    }

    public void fireChangeServerVersion(String version) {
        Intent intent = new Intent(DOWNGRADE_SERVER_VERSION);
        intent.putExtra("target_version", version);
        mContext.sendBroadcast(intent);
    }

    public void fireChangeServerEdition() {
        Intent intent = new Intent(CHANGE_SERVER_EDITION);
        intent.putExtra("edition_version", "CE");
        mContext.sendBroadcast(intent);
    }

}
