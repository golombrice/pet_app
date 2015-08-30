package com.example.petri.myapplication;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by petri on 17/08/15.
 */


public class GCMTokenRegisterService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public GCMTokenRegisterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences prefs = getSharedPreferences("omat", 0);
        String token = null;
        int user_id = 0;

        while( token == null || user_id == 0 ) {
            token = prefs.getString("token", null);
            user_id = prefs.getInt("my_user_id", 0);
        }
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String gcm_token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + gcm_token);

                // TODO: Implement this method to send any registration to your app's servers.
                sendRegistrationToServer(gcm_token, user_id, token);
//                SharedPreferences prefs = getSharedPreferences("omat", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("gcm_token", gcm_token);
                editor.commit();
//                sendMessage();

                // Subscribe to topic channels
                subscribeTopics(gcm_token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
//                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                // [END register_for_gcm]
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
//            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String gcm_token, int user_id, String token) {
        String file = "register_token.php";

        JSONObject msg = new JSONObject();
        try {
            msg.put("user_id", user_id);
            msg.put("google_token", token);
            msg.put("token", gcm_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//         = "user_id=" + user_id + "&google_token=" + token + "&token=" + gcm_token;
        Log.d("sendGCMTokentoserver", "fdsfds");

        Utilities.sendPostRequest(file, msg);


//        URL urlToRequest;
//        HttpURLConnection urlConnection;
//        try {
//            urlToRequest = new URL(urlStr);
//        } catch (java.net.MalformedURLException e) {
//            return;
//        }
//        try {
//            urlConnection =
//                    (HttpURLConnection) urlToRequest.openConnection();
//        } catch (java.io.IOException e) {
//            return;
//        }
//        urlConnection.setDoOutput(true);
//        try {
//            urlConnection.setRequestMethod("POST");
//        } catch (java.net.ProtocolException e) {
//            return;
//        }
//        urlConnection.setRequestProperty("Content-Type",
//                "application/x-www-form-urlencoded");
//        urlConnection.setFixedLengthStreamingMode(
//                postParameters.getBytes().length);
//
//        PrintWriter out;
//        try {
//            out = new PrintWriter(urlConnection.getOutputStream());
//        } catch (java.io.IOException e) {
//            return;
//        }
//
//        out.print(postParameters);
//        out.close();
//
//        // handle issues
//        int statusCode = 0;
//        try {
//         statusCode = urlConnection.getResponseCode();
//        } catch (java.io.IOException e) {
//            return;
//        }
//        if (statusCode == HttpURLConnection.HTTP_OK) {
//            Log.d("fds", "ok");
//        }
    }


    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
