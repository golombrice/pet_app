package com.example.petri.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by petri on 19/08/15.
 */
public class MainService extends Service {
//    public MainService() {
//        super("MainService");
//    }

    public static final String PARAM_OUT_MSG = "omsg";

    public class PopulateBinder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }

    private final PopulateBinder binder_ = new PopulateBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder_;
    }


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
                    jusers.get(i).setMessage(json.getJSONObject(i).getJSONArray("data").getString(2));
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


    public void getAmIRegistered() {


        Log.d("mainservice", "fdssdfsdfsdf");
        new registerCheckOperation().execute();
    }

    //    private int get_user_operation_counter_ = 0;
    private class registerCheckOperation extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... arg0) {

            SharedPreferences prefs = getSharedPreferences("omat", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("my_user_id", 3);
            editor.commit();

            String token = null;
            while( token == null ) {
                token = prefs.getString("token", null);
            }
            Log.d("registerCheckOp", "fds");
            return Utilities.sendGetRequest("validate_token.php", "token=" + token);

        }


        @Override
        protected void onPostExecute(JSONObject result) {
//                TextView txt = (TextView) findViewById(R.id.output);
//                txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            Log.d("populateuserservice", result.toString());

            try {
                if( result.getString("user_found").equals("false") ) {
                    listener_.receiveNotRegisteredNotification();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int my_user_id = 0;
            try {
                my_user_id = Integer.parseInt(result.getString("user_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener_.receiveRegistrationInfo(my_user_id);

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

    public void createUser() {
        new userCreateOperation().execute();
    }

    private class userCreateOperation extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            JSONObject msg = new JSONObject();
            try {
                msg.put("token", getSharedPreferences("omat", 0).getString("token", "lol"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Utilities.sendPostRequest("create_user.php", msg);

            return null;
        }


        @Override
        protected void onPostExecute(Void arg0) {
            getAmIRegistered();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void getUsers() {
        new userReceiveOperation().execute();
    }

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