package com.plugin.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.iid.InstanceID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

public class PushPluginNotificationManager extends CordovaPlugin {
    private static final String TAG = PushPluginNotificationManager.class.getSimpleName();
    private static JSONObject categories;

    public static final String ACTION_KEY = "PUSH_NOTIFICATION_ACTION_IDENTIFIER";

    public static void registerCategories(JSONObject categories) {
        PushPluginNotificationManager.categories = categories;
    }

    public static void create(Context context, Bundle extras) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String appName = PushPluginUtil.getAppName(context);

        Intent notificationIntent = new Intent(context, PushHandlerActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("pushBundle", extras);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int defaults = Notification.DEFAULT_ALL;

        if (extras.getString("defaults") != null) {
            try {
                defaults = Integer.parseInt(extras.getString("defaults"));
            } catch (NumberFormatException e) {}
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setDefaults(defaults)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(extras.getString("title"))
                .setTicker(extras.getString("title"))
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        String message = extras.getString("message");
        if (message != null) {
            mBuilder.setContentText(message);
        } else {
            mBuilder.setContentText("<missing message content>");
        }

        String msgcnt = extras.getString("msgcnt");
        if (msgcnt != null) {
            mBuilder.setNumber(Integer.parseInt(msgcnt));
        }

        int notId = 0;

        try {
            notId = Integer.parseInt(extras.getString("notId"));
        }
        catch(NumberFormatException e) {
            Log.e(TAG, "Number format exception - Error parsing Notification ID: " + e.getMessage());
        }
        catch(Exception e) {
            Log.e(TAG, "Number format exception - Error parsing Notification ID" + e.getMessage());
        }

        String cat_id = extras.getString("categoryName");
        if(cat_id != null) {
            JSONObject category = PushPluginNotificationManager.categories.optJSONObject(cat_id);
            JSONArray actions   = category.optJSONArray("actions");
            int requestCode;

            // add each action
            for(int i = 0; i < actions.length(); i++) {
                JSONObject action = actions.optJSONObject(i);
                requestCode = new Random().nextInt();

                // set up correct receiver for foreground or background handler
                //if(extras.getString("activationMode") == "background") {
                // notificationIntent = new Intent(this, BackgroundNotificationReceiver.class);
                //}
                //else {
                notificationIntent = new Intent(context, PushHandlerActivity.class);
                //}
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                notificationIntent.putExtra("pushBundle", extras);
                notificationIntent.putExtra("notId", notId);
                notificationIntent.putExtra(ACTION_KEY, action.optString("identifier"));

                contentIntent = PendingIntent.getBroadcast(context, requestCode, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                mBuilder  = mBuilder.addAction(action.optInt("icon"), action.optString("title"), contentIntent);
            }
        }

        mNotificationManager.notify(appName, notId, mBuilder.build());
    }

    public static void cancelAll(Context context) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void cancel(Context context, int notificationId) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

}
