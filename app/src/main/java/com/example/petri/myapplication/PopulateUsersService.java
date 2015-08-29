package com.example.petri.myapplication;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by petri on 19/08/15.
 */
public class PopulateUsersService extends Service {
//    public PopulateUsersService() {
//        super("PopulateUsersService");
//    }

    public static final String PARAM_OUT_MSG = "omsg";

    public class PopulateBinder extends Binder {
        PopulateUsersService getService() {
            return PopulateUsersService.this;
        }
    }

    private final PopulateBinder binder_ = new PopulateBinder();


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder_;
    }


//    protected void onHandleIntent(Intent intent) {
//        String users = new String();
//        try {
//            users = getUsers();
//            Log.d("fdsa", users);
//        } catch (java.io.IOException e) {
//        }
//
//        try {
//
//
//            JSONArray json = new JSONArray(users);
//            ArrayList<User> jusers = new ArrayList<User>();
//
//            for (int i = 0; i < json.length(); ++i) {
//                Log.d("fds", json.getJSONObject(i).getJSONArray("data").getString(1));
//                jusers.add(i, new User());
//                jusers.get(i).setName(json.getJSONObject(i).getJSONArray("data").getString(1));
//                jusers.get(i).setId(Integer.parseInt(json.getJSONObject(i).getJSONArray("data").getString(0)));
//
//            }
//
//
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction(MainActivity.userResponseReceiver.ACTION_RESP);
//            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//            broadcastIntent.putExtra(PARAM_OUT_MSG, jusers);
//            sendBroadcast(broadcastIntent);
//        } catch (org.json.JSONException e) {
//            Log.d("fds", "fds");
//        }
//
//    }

    private class userReceiveOperation extends AsyncTask<Void, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(Void... arg0) {
            String urlStr = "http://80.222.146.25/~arkkaaja/get_all_users.php";
            URL urlToRequest;
            HttpURLConnection urlConnection;
            try {
                urlToRequest = new URL(urlStr);
            } catch (java.net.MalformedURLException e) {
                return null;
            }
            try {
                urlConnection =
                        (HttpURLConnection) urlToRequest.openConnection();
            } catch (java.io.IOException e) {
                return null;
            }
            try {
                urlConnection.setRequestMethod("GET");
            } catch (java.net.ProtocolException e) {
                return null;
            }
            try {
                // handle issues
                int statusCode = 0;
                statusCode = urlConnection.getResponseCode();
                Log.d("getuserinfo", Integer.toString(statusCode));

                if (statusCode == HttpURLConnection.HTTP_OK) {
                    Log.d("fds", "ok");
                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;

                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                return jsonStringToArray(response.toString());
            } catch (java.io.IOException e) {

            }

            return null;

        }

        private ArrayList<User> jsonStringToArray(String users) {
            ArrayList<User> jusers = new ArrayList<User>();

            try {


                JSONArray json = new JSONArray(users);

                for (int i = 0; i < json.length(); ++i) {
                    Log.d("fds", json.getJSONObject(i).getJSONArray("data").getString(1));
                    jusers.add(i, new User());
                    jusers.get(i).setName(json.getJSONObject(i).getJSONArray("data").getString(1));
                    jusers.get(i).setId(Integer.parseInt(json.getJSONObject(i).getJSONArray("data").getString(0)));

                }

            } catch (org.json.JSONException e) {
                Log.d("fds", "fds");
            }
            return jusers;
        }

        @Override
        protected void onPostExecute(ArrayList<User> result) {
//                TextView txt = (TextView) findViewById(R.id.output);
//                txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            listener_.receiverUserList(result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public int getUser() {
        return 0;
    }

    //    private int get_user_operation_counter_ = 0;
//    private class getUserOperation extends AsyncTask<String, Void, User > {
//
//        @Override
//        protected User doInBackground(String... arg0) {
//            String urlStr = "http://80.222.146.25/~arkkaaja/get_users.php";
//            URL urlToRequest;
//            HttpURLConnection urlConnection;
//            try {
//                urlToRequest = new URL(urlStr);
//            } catch (java.net.MalformedURLException e) {
//            }
//            try {
//                urlConnection =
//                        (HttpURLConnection) urlToRequest.openConnection();
//            } catch (java.io.IOException e) {
//                return 0;
//            }
//            urlConnection.setDoOutput(true);
//            try {
//                urlConnection.setRequestMethod("POST");
//            } catch (java.net.ProtocolException e) {
//                return 0;
//            }
//            urlConnection.setRequestProperty("Content-Type",
//                    "application/json");
//
//
//            PrintWriter out;
//            try {
//                out = new PrintWriter(urlConnection.getOutputStream());
//            } catch (java.io.IOException e) {
//                return 0;
//            }
//
//            JSONObject msg = new JSONObject();
//            try {
//                msg.put("message", message[0]);
//                msg.put("from", Integer.toString(from_id_));
//                msg.put("to", Integer.toString(to_id_));
//            } catch (org.json.JSONException e) {
//                return 0;
//            }
//
//            out.print(msg.toString());
//            out.close();
//        }
    public void getAmIRegistered() {
        SharedPreferences prefs = getSharedPreferences("omat", 0);
        String token = prefs.getString("token", "lol");
        Log.d("populateservice", token);
        new registerCheckOperation().execute(token);
    }

    //    private int get_user_operation_counter_ = 0;
    private class registerCheckOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {
            String urlStr = "http://80.222.146.25/~arkkaaja/validate_token.php";
            URL urlToRequest;
            HttpURLConnection urlConnection;
            try {
                urlToRequest = new URL(urlStr);
            } catch (java.net.MalformedURLException e) {
                e.printStackTrace();

                return "err";
            }
            try {
                urlConnection =
                        (HttpURLConnection) urlToRequest.openConnection();
            } catch (java.io.IOException e) {
                e.printStackTrace();

                return "err";
            }
            urlConnection.setDoOutput(true);
            try {
                urlConnection.setRequestMethod("POST");
            } catch (java.net.ProtocolException e) {
                e.printStackTrace();
                return "err";
            }
            String postParameters = "token=" + arg0[0];
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            urlConnection.setFixedLengthStreamingMode(
                    postParameters.getBytes().length);


            PrintWriter out;
            try {
                out = new PrintWriter(urlConnection.getOutputStream());
            } catch (java.io.IOException e) {
                e.printStackTrace();

                return "err";
            }




            out.print(postParameters);
            out.close();

            try {
                int statusCode = 0;
                statusCode = urlConnection.getResponseCode();
                Log.d("populateservice", Integer.toString(statusCode));
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    Log.d("fds", "ok");
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();

                return "err";
            }

            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;

                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("populateservice", response.toString());
                return response.toString();

            } catch (java.io.IOException e) {
                e.printStackTrace();

                return "err";
            }

        }


        @Override
        protected void onPostExecute(String result) {
//                TextView txt = (TextView) findViewById(R.id.output);
//                txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            Log.d("populateuserservice", result);
            if (result.equals("normal_user")) {
                listener_.receiveRegistrationInfo(true, false);
            }
            if (result.equals("carer")) {
                listener_.receiveRegistrationInfo(true, true);
            }
            if (result.equals("err")) {
                listener_.receiveRegistrationInfo(false, false);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    UserInfoListener listener_;

    public void registerListener(UserInfoListener listener) {
        listener_ = listener;
    }

    public void getUsers() {
        new userReceiveOperation().execute();
    }
//
//    private String getUsers() throws java.io.IOException {
//        String urlStr = "http://80.222.146.25/~arkkaaja/get_all_users.php";
//        URL urlToRequest;
//        HttpURLConnection urlConnection;
//        try {
//            urlToRequest = new URL(urlStr);
//        } catch (java.net.MalformedURLException e) {
//            return "err";
//        }
//        try {
//            urlConnection =
//                    (HttpURLConnection) urlToRequest.openConnection();
//        } catch (java.io.IOException e) {
//            return "err";
//        }
//        try {
//            urlConnection.setRequestMethod("GET");
//        } catch (java.net.ProtocolException e) {
//            return "err";
//        }
//
//        // handle issues
//        int statusCode = 0;
//        try {
//            statusCode = urlConnection.getResponseCode();
//        } catch (java.io.IOException e) {
//            return "err";
//        }
//        if (statusCode == HttpURLConnection.HTTP_OK) {
//            Log.d("fds", "ok");
//        }
//
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(urlConnection.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        return response.toString();
//
////        InputStream response;
////        try {
////            response = urlConnection.getInputStream();
////            response.close();
////
////        } catch (java.io.IOException e) {
////            return "err";
////        }
////
////        return convertStreamToString(response);
//
//
//    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}