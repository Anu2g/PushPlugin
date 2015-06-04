package com.plugin.gcm;

import android.content.Context;

public class PushPluginUtil {

    public static String getAppName(Context context) {
        CharSequence appName = context
                .getPackageManager()
                .getApplicationLabel(context.getApplicationInfo());

        return (String) appName;
    }
}
