package com.plugin.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class PushPluginGcmListenerService extends GcmListenerService {
    private static final String TAG = PushPluginGcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "onMessage - context: " + this.getApplicationContext());
        this.handleMessage(data);
    }

    private void handleMessage(Bundle extras) {
        Log.v(TAG, "extras: " + extras.toString());

        if (extras != null) {
            // if we are in the foreground, just surface the payload, else post it to the statusbar
            if (PushPlugin.isInForeground()) {
                extras.putBoolean("foreground", true);
                PushPlugin.sendExtras(extras);
            } else {
                extras.putBoolean("foreground", false);

                // Send a notification if there is a message
                if (extras.getString("message") != null && extras.getString("message").length() != 0) {
                    PushPluginNotificationManager.create(this.getApplicationContext(), extras);
                }
            }
        }
    }



}