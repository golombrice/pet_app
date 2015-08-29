package com.example.petri.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements UserInfoListener {

    private ProgressDialog dialog;

    @Override
    public void receiverUserList(ArrayList< User > users_parc) {
        Log.d("hei", "moi");
//        final ArrayList<User> users_parc = intent.getParcelableArrayListExtra(PopulateUsersService.PARAM_OUT_MSG);
        Log.d("activity", users_parc.toString());
        LinearLayout user_list = (LinearLayout) findViewById(R.id.userList);

        for( int i = 0; i < users_parc.size(); ++i)
        {
            Log.d("activ", Integer.toString(users_parc.get(i).getId()) + " " + users_parc.get(i).getName());
            TextView name_field = new TextView(MainActivity.this);
            name_field.setText(users_parc.get(i).getName());
            name_field.setTextSize(25);
            final int user_id = users_parc.get(i).getId();
            name_field.setOnClickListener(// Create an anonymous implementation of OnClickListener
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            // do something when the button is clicked
                            TextView tv = (TextView) v;
                            tv.setTextColor(getResources().getColor(R.color.blue));
                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.putExtra("chat_id", user_id);
                            startActivity(intent);
                        }
                    });
            user_list.addView(name_field);
            users_fetched_ = true;

        }


        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    @Override
    public void receiveRegistrationInfo(boolean registered, boolean carer) {
        Log.d("mainactivity", "receive" + registered + carer);


    }

    private boolean users_fetched_ = false;


//    public class userResponseReceiver extends BroadcastReceiver {
//        public static final String ACTION_RESP = "com.example.petri.MESSAGE_PROCESSED";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d("hei", "moi");
//            final ArrayList<User> users_parc = intent.getParcelableArrayListExtra(PopulateUsersService.PARAM_OUT_MSG);
//            Log.d("activity", users_parc.toString());
//            LinearLayout user_list = (LinearLayout) findViewById(R.id.userList);
//
//            for( int i = 0; i < users_parc.size(); ++i)
//            {
//                Log.d("activ", Integer.toString(users_parc.get(i).getId()) + " " + users_parc.get(i).getName());
//                TextView name_field = new TextView(MainActivity.this);
//                name_field.setText(users_parc.get(i).getName());
//                name_field.setTextSize(25);
//                final int user_id = users_parc.get(i).getId();
//                name_field.setOnClickListener(// Create an anonymous implementation of OnClickListener
//                        new View.OnClickListener() {
//                            public void onClick(View v) {
//                                // do something when the button is clicked
//                                TextView tv = (TextView) v;
//                                tv.setTextColor(getResources().getColor(R.color.blue));
//                                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
//                                intent.putExtra("chat_id", user_id);
//                                startActivity(intent);
//                            }
//                        });
//                user_list.addView(name_field);
//            }
//
//
//        }
//    }

    private PopulateUsersService service_;
    private boolean bound_ = false;

    private ServiceConnection connection_ = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PopulateUsersService.PopulateBinder binder = (PopulateUsersService.PopulateBinder) service;
            service_ = binder.getService();
            bound_ = true;
            service_.registerListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            bound_ = false;
        }
    };

    //    private userResponseReceiver receiver;
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, PopulateUsersService.class);

        bindService(intent, connection_, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound_) {
            unbindService(connection_);
            bound_ = false;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        IntentFilter filter = new IntentFilter(userResponseReceiver.ACTION_RESP);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);
//        receiver = new userResponseReceiver();
//        registerReceiver(receiver, filter);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.show();
        new checkThatAllInfoIsSetOperation().execute();

        Intent intent = new Intent(this, PopulateUsersService.class);
        startService(intent);




        Button button = (Button) findViewById(R.id.fetch_users_button);
        button.setOnClickListener(// Create an anonymous implementation of OnClickListener
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(!users_fetched_) {
                            service_.getAmIRegistered();
                            service_.getUsers();
                        }

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class checkThatAllInfoIsSetOperation extends AsyncTask<Void, Void, Void> {


        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(final Void... args) {
            SharedPreferences prefs = getSharedPreferences("omat", 0);
            while (true) {
                if (prefs.contains("token")) {
                    Log.d("fds", "token found");

                    Intent intent2 = new Intent(MainActivity.this, RegistrationIntentService.class);
                    startService(intent2);
                    while( true ) {
                        if (prefs.contains("gcm_token") && bound_) {
                            Log.d("fds", "gcm_token found");

                            break;
                        }
                        Intent intent3 = new Intent(MainActivity.this, MyGcmListenerService.class);
                        startService(intent3);
                    }
                    break;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void p) {

            if(!users_fetched_) {
                service_.getAmIRegistered();
                service_.getUsers();
            }

            // Setting data to list adapter
//            setListData();
        }
    }
}
