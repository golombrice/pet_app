package com.example.petri.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class TabcontainerActivity extends AppCompatActivity implements UserInfoListener {

    private ProgressDialog dialog;
    private MainService service_;
    private boolean bound_ = false;
    private boolean users_fetched_ = false;

    private HashSet< Integer > pending_requests_ = new HashSet<>();

    private ServiceConnection connection_ = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.PopulateBinder binder = (MainService.PopulateBinder) service;
            service_ = binder.getService();
            bound_ = true;
            service_.registerListener(TabcontainerActivity.this);
//            new checkThatAllInfoIsSetOperation().execute();
            if( !users_fetched_ ) {
                int request_id = service_.getAmIRegistered();
                pending_requests_.add(request_id);
            }

//            for(int i=0; i < pager_adapter_.getCount(); ++i ) {
                ((serviceConnectionListener) pager_adapter_.getFragment(0)).onConnected(service_);
//            }
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

        Intent intent = new Intent(this, MainService.class);

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

    private SampleFragmentPagerAdapter pager_adapter_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        pager_adapter_ = new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                TabcontainerActivity.this);
        viewPager.setAdapter(pager_adapter_);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

//        IntentFilter filter = new IntentFilter(userResponseReceiver.ACTION_RESP);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);
//        receiver = new userResponseReceiver();
//        registerReceiver(receiver, filter);


        Intent intent = new Intent(this, MainService.class);
        startService(intent);

        Intent intent3 = new Intent(TabcontainerActivity.this, GCMMessageListenerService.class);
        startService(intent3);


    }


    @Override
    public void receiverUserList(int request_id, ArrayList< User > users_parc) {
        if ( !pending_requests_.contains(request_id) ) {
            return;
        }

        pending_requests_.remove(request_id);


    }




    @Override
    public void receiveRegistrationInfo(int request_id, int user_id) {
        if ( !pending_requests_.contains(request_id) ) {
            Log.d("receiveRegisterInfo", Integer.toString(request_id));
            return;
        }

        pending_requests_.remove(request_id);
        Log.d("mainactivity", "receive" + user_id);
        SharedPreferences prefs = getSharedPreferences("omat", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("my_user_id", user_id);
        editor.commit();

        Intent intent2 = new Intent(TabcontainerActivity.this, GCMTokenRegisterService.class);
        startService(intent2);


        pending_requests_.add(service_.getUsers());
    }

    @Override
    public void receiveNotRegisteredNotification(int request_id) {
        if ( !pending_requests_.contains(request_id) ) {
            return;
        }

        pending_requests_.remove(request_id);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        pending_requests_.add(service_.createUser());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Et ole käyttänyt Tassua aikaisemmin. Haluatko rekisteröityä?").setPositiveButton("Kyllä", dialogClickListener)
                .setNegativeButton("Ei", dialogClickListener).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabcontainer, menu);
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
}
