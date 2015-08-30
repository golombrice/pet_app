/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.petri.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.content.LocalBroadcastManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.android.gms.gcm.GcmListenerService;

public class GCMMessageListenerService extends GcmListenerService {

    private static final String TAG = "GCMMessageListener";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        int from_id = Integer.parseInt(data.getString("from_id"));
        int to_id = Integer.parseInt(data.getString("to_id"));
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("message", message);
        values.put("from_id", from_id);
        values.put("to_id", to_id);


// Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                "messages",
                "message_id",
                values);

//        SQLiteDatabase dbr = helper.getReadableDatabase();

//        Cursor cursor = dbr.query(false, "messages", new String[]{"message"}, null, null, null, null, null, null);
//        cursor.moveToLast();
//        String this_message = cursor.getString(
//                cursor.getColumnIndexOrThrow("message")
//        );
//        Log.d("mygcm", this_message);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ChatActivity.MessageResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("from_id", from_id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

//        SQLiteDatabase mydatabase =
//        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS messages(message_id INT auto_increment, message TEXT, primary key(message_id));");
//        mydatabase.execSQL("INSERT INTO TutorialsPoint VALUES('null','" + message + "');");
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, from_id);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, int from_id) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("chat_id", from_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
