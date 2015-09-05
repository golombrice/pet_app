//package com.example.petri.myapplication;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.LinearLayout;
//
//import java.util.ArrayList;
//
//public class MainActivity extends Activity implements UserInfoListener {
//
//    private ProgressDialog dialog;
//    private MainService service_;
//    private boolean bound_ = false;
//    private boolean users_fetched_ = false;
//
//    private ServiceConnection connection_ = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            MainService.PopulateBinder binder = (MainService.PopulateBinder) service;
//            service_ = binder.getService();
//            bound_ = true;
//            service_.registerListener(MainActivity.this);
////            new checkThatAllInfoIsSetOperation().execute();
//            if( !users_fetched_ ) {
//                service_.getAmIRegistered();
//            }
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName className) {
//            bound_ = false;
//        }
//    };
//
//    //    private userResponseReceiver receiver;
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        Intent intent = new Intent(this, MainService.class);
//
//        bindService(intent, connection_, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (bound_) {
//            unbindService(connection_);
//            bound_ = false;
//        }
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
////        IntentFilter filter = new IntentFilter(userResponseReceiver.ACTION_RESP);
////        filter.addCategory(Intent.CATEGORY_DEFAULT);
////        receiver = new userResponseReceiver();
////        registerReceiver(receiver, filter);
//        dialog = new ProgressDialog(this);
//        dialog.setMessage("Please wait");
//        dialog.show();
//
//        Intent intent = new Intent(this, MainService.class);
//        startService(intent);
//
//        Intent intent3 = new Intent(MainActivity.this, GCMMessageListenerService.class);
//        startService(intent3);
//
////
////        Button button = (Button) findViewById(R.id.fetch_users_button);
////        button.setOnClickListener(// Create an anonymous implementation of OnClickListener
////                new View.OnClickListener() {
////                    public void onClick(View v) {
////                        if(!users_fetched_) {
////                            service_.getAmIRegistered();
////                            service_.getUsers();
////                        }
////
////                    }
////                });
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void receiverUserList(ArrayList< User > users_parc) {
//        Log.d("hei", "moi");
////        final ArrayList<User> users_parc = intent.getParcelableArrayListExtra(MainService.PARAM_OUT_MSG);
//        Log.d("activity", users_parc.toString());
//        LinearLayout user_list = (LinearLayout) findViewById(R.id.userList);
//
//        for( int i = 0; i < users_parc.size(); ++i)
//        {
//            Log.d("activ", Integer.toString(users_parc.get(i).getId()) + " " + users_parc.get(i).getName());
//            UserView name_field = new UserView(MainActivity.this);
////            TextView name_field = new TextView(MainActivity.this);
//            name_field.setName(users_parc.get(i).getName());
//            name_field.setMessage(users_parc.get(i).getMessage());
////            name_field.setTextSize(25);
//            final int user_id = users_parc.get(i).getId();
//            name_field.setOnClickListener(// Create an anonymous implementation of OnClickListener
//                    new View.OnClickListener() {
//                        public void onClick(View v) {
//                            // do something when the button is clicked
//                            UserView tv = (UserView) v;
//                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
//                            intent.putExtra("chat_id", user_id);
//                            startActivity(intent);
//                        }
//                    });
//            user_list.addView(name_field);
//
//        }
//
//        users_fetched_ = true;
//
//
//
//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
//
//    }
//
//
//    @Override
//    public void receiveRegistrationInfo(int user_id) {
//        Log.d("mainactivity", "receive" + user_id);
//        SharedPreferences prefs = getSharedPreferences("omat", 0);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putInt("my_user_id", user_id);
//        editor.commit();
//
//        Intent intent2 = new Intent(MainActivity.this, GCMTokenRegisterService.class);
//        startService(intent2);
//
//
//        service_.getUsers();
//    }
//
//    @Override
//    public void receiveNotRegisteredNotification() {
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case DialogInterface.BUTTON_POSITIVE:
//                        service_.createUser();
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        //No button clicked
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Et ole käyttänyt Tassua aikaisemmin. Haluatko rekisteröityä?").setPositiveButton("Kyllä", dialogClickListener)
//                .setNegativeButton("Ei", dialogClickListener).show();
//    }
//
//    private class checkThatAllInfoIsSetOperation extends AsyncTask<Void, Void, Void> {
//
//
//        /** progress dialog to show user that the backup is processing. */
//        /**
//         * application context.
//         */
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected Void doInBackground(final Void... args) {
//            SharedPreferences prefs = getSharedPreferences("omat", 0);
//            dialog.setTitle("Acquiring Google token");
//
//            while (true) {
//                if (prefs.contains("token")) {
//                    Log.d("MainActivity", "token found");
//
//
//                    break;
//                }
//            }
//
//            dialog.setTitle("Checking user registration status");
//
//            while( true ) {
//                if (prefs.contains("my_user_id")) {
//                    Log.d("MainActivity", "My user id found");
//
//
//                    break;
//                }
//            }
//
//            dialog.setTitle("Acquiring Google Cloud Messaging token");
//
//
//            while( true ) {
//                if (prefs.contains("gcm_token") && bound_) {
//
//                    break;
//                }
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void p) {
//
//
////            if(!users_fetched_) {
////                service_.getUsers();
////            }
//
//            // Setting data to list adapter
////            setListData();
//        }
//    }
//}
