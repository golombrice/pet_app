package com.example.petri.myapplication;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import android.support.v4.content.LocalBroadcastManager;

public class ChatService extends Service {

//    private String from_id_;
    private int to_id_;

    public class ChatBinder extends Binder {
        ChatService getService() {
            return ChatService.this;
        }
    }

    private final ChatBinder binder_ = new ChatBinder();

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        SharedPreferences prefs = getSharedPreferences("omat", 0);
//        from_id_ = prefs.getString("token", null);
        to_id_ = intent.getIntExtra("to_id", 0);
        return binder_;
    }

    public void sendMessage(String message) {
        new messageOperation().execute(message);
    }


    private class messageOperation extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... message) {
            String urlStr = "http://80.222.146.25/~arkkaaja/send_message.php";
            URL urlToRequest;
            HttpURLConnection urlConnection;
            try {
                urlToRequest = new URL(urlStr);
            } catch (java.net.MalformedURLException e) {
                return 0;
            }
            try {
                urlConnection =
                        (HttpURLConnection) urlToRequest.openConnection();
            } catch (java.io.IOException e) {
                return 0;
            }
            urlConnection.setDoOutput(true);
            try {
                urlConnection.setRequestMethod("POST");
            } catch (java.net.ProtocolException e) {
                return 0;
            }
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");


            PrintWriter out;
            try {
                out = new PrintWriter(urlConnection.getOutputStream());
            } catch (java.io.IOException e) {
                return 0;
            }

            JSONObject msg = new JSONObject();
            try {
                msg.put("message", message[0]);
                msg.put("from", getSharedPreferences("omat", 0).getInt("my_user_id", 0));
                msg.put("to", Integer.toString(to_id_));
            } catch (org.json.JSONException e) {
                return 0;
            }

            out.print(msg.toString());
            out.close();

            DatabaseHelper helper = new DatabaseHelper(ChatService.this);
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("message", message[0]);
            values.put("to_id", Integer.toString(to_id_));
            values.put("from_id", getSharedPreferences("omat", 0).getInt("my_user_id", 0));


// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    "messages",
                    "message_id",
                    values);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ChatActivity.MessageResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra("from_id", getSharedPreferences("omat", 0).getInt("my_user_id", 0));
            LocalBroadcastManager.getInstance(ChatService.this).sendBroadcast(broadcastIntent);


            // handle issues
            int statusCode = 0;
            try {
                statusCode = urlConnection.getResponseCode();
            } catch (java.io.IOException e) {
                return 0;
            }
            if (statusCode == HttpURLConnection.HTTP_OK) {
                Log.d("fds", "ok");
            }
            return 1;

        }

        @Override
        protected void onPostExecute(Integer result) {
//                TextView txt = (TextView) findViewById(R.id.output);
//                txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    public void getName() {
        new getNameOperation().execute();
    }


    private class getNameOperation extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... arg0) {
            return Utilities.sendGetRequest("get_name_by_id.php", "user_id="+to_id_);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ChatActivity.NameResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            try {
                broadcastIntent.putExtra("name", result.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LocalBroadcastManager.getInstance(ChatService.this).sendBroadcast(broadcastIntent);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
