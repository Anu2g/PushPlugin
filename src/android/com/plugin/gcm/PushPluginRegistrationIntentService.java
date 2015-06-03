package com.plugin.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class PushPluginRegistrationIntentService extends IntentService {
    private static final String TAG = PushPluginRegistrationIntentService.class.getSimpleName();

    private static final Object lock = new Object();

    public PushPluginRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String senderId = intent.getStringExtra("senderId");
        this.register(senderId);
    }

    private void register(String senderId) {
        JSONObject json;
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (lock) {
                json = new JSONObject().put("event", "registered");
                InstanceID instanceID = InstanceID.getInstance(this);
                String regId = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                json.put("regid", regId);

                Log.v(TAG, "onRegistered: " + json.toString());

                // Send this JSON data to the JavaScript application above EVENT should be set to the msg type
                // In this case this is the registration ID
                PushPlugin.sendJavascript(json);
            }
        } catch (JSONException e) {
            // No message to the user is sent, JSON failed
            Log.e(TAG, "onRegistered: JSON exception");
        } catch (IOException e) {
            Log.e(TAG, "onRegistered: IOException");
        }
    }
}