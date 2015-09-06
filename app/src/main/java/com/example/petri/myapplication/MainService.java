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


    private int requestid_ = 0;
    private ArrayList<UserInfoListener> listeners_ = new ArrayList<UserInfoListener>();

    public void registerListener(UserInfoListener listener) {
        listeners_.add(listener);
    }

    public int getAmIRegistered() {
        Log.d("mainservice", "fdssdfsdfsdf");
        new registerCheckOperation().execute(requestid_);
        return requestid_++;
    }

    public int createUser() {
        new userCreateOperation().execute(requestid_);
        return requestid_++;
    }

    public int getUsers() {
        new userReceiveOperation().execute(requestid_);
        return requestid_++;

    }

    private class userCreateOperation extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... arg0) {

            JSONObject msg = new JSONObject();
            try {
                msg.put("token", getSharedPreferences("omat", 0).getString("token", "lol"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Utilities.sendPostRequest("create_user.php", msg);

            return arg0[0];
        }


        @Override
        protected void onPostExecute(Integer arg0) {
            getAmIRegistered();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class OperationReturnValue {
        OperationReturnValue(JSONObject o, int r) {
            object = o;
            request_id = r;
        }
        public JSONObject object;
        public int request_id;
    }

    private class userReceiveOperation extends AsyncTask<Integer, Void, OperationReturnValue> {

        @Override
        protected OperationReturnValue doInBackground(Integer... arg0) {
            String file = "get_all_users.php";

            JSONObject users = Utilities.sendGetRequest(file, "");

            return new OperationReturnValue(users, arg0[0]);
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
        protected void onPostExecute(OperationReturnValue result) {
//                TextView txt = (TextView) findViewById(R.id.output);
//                txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto youJSONArray userlist = null;    ArrayList< User > user_array = new ArrayList< User >();
            ArrayList< User > user_array = new ArrayList< User >();

            JSONObject users = result.object;
            int request_id = result.request_id;

            JSONArray userlist = null;
            try {
                userlist = users.getJSONArray("users");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for( int i = 0; i < userlist.length(); ++i ) {
                try {
                    JSONObject user_this = null;

                    user_this = userlist.getJSONObject(i);

                    user_array.add(i, new User());
                    user_array.get(i).setName(user_this.getString("name"));
                    user_array.get(i).setMessage(user_this.getString("comment"));
                    user_array.get(i).setId(user_this.getInt("user_id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for( int i = 0; i < listeners_.size(); ++ i ) {
                listeners_.get(i).receiverUserList(request_id, user_array);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    //    private int get_user_operation_counter_ = 0;
    private class registerCheckOperation extends AsyncTask<Integer, Void, OperationReturnValue> {

        @Override
        protected OperationReturnValue doInBackground(Integer... arg0) {

            SharedPreferences prefs = getSharedPreferences("omat", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("my_user_id", 3);
            editor.commit();

            String token = null;
            while( token == null ) {
                token = prefs.getString("token", null);
            }
            Log.d("registerCheckOp", "fds");
            JSONObject json =  Utilities.sendGetRequest("validate_token.php", "token=" + token);

            return new OperationReturnValue(json, arg0[0]);
        }


        @Override
        protected void onPostExecute(OperationReturnValue r) {
//                TextView txt = (TextView) findViewById(R.id.output);
//                txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            JSONObject result = r.object;
            Log.d("populateuserservice", result.toString());

            try {
                if( result.getString("user_found").equals("false") ) {
                    for( int i = 0; i < listeners_.size(); ++ i ) {
                        listeners_.get(i).receiveNotRegisteredNotification(r.request_id);
                    }
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
            for( int i = 0; i < listeners_.size(); ++i) {
                listeners_.get(i).receiveRegistrationInfo(r.request_id, my_user_id);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}