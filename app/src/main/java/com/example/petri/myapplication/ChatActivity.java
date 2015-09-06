package com.example.petri.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import android.support.v4.content.LocalBroadcastManager;

public class ChatActivity extends AppCompatActivity {

    private ChatService service_;
    private boolean bound_ = false;

    private ServiceConnection connection_ = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ChatService.ChatBinder binder = (ChatService.ChatBinder) service;
            service_ = ((ChatService.ChatBinder) service).getService();
            bound_ = true;
            service_.getName();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            bound_ = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ChatService.class);
        intent.putExtra("to_id", chat_partner_id_);

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

    private DatabaseHelper helper_;
    private int newest_message_id_ = 0;
    private LinearLayout chatContainer_;
    private MessageResponseReceiver receiver_;
    private NameResponseReceiver name_receiver_;

    private int chat_partner_id_ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat_partner_id_ = getIntent().getIntExtra("chat_id", 0);

        IntentFilter filter = new IntentFilter(MessageResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver_ = new MessageResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver_, filter);

        IntentFilter filter2 = new IntentFilter(NameResponseReceiver.ACTION_RESP);
        filter2.addCategory(Intent.CATEGORY_DEFAULT);
        name_receiver_ = new NameResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(name_receiver_, filter2);


        chatContainer_ = (LinearLayout) findViewById(R.id.chatcontainer);

        helper_ = new DatabaseHelper(this);

        getNewMessages();

        Button button = (Button) findViewById(R.id.send_button);

        button.setOnClickListener(// Create an anonymous implementation of OnClickListener
                new View.OnClickListener() {
                    public void onClick(View v) {
                        EditText message_field = (EditText) findViewById(R.id.message);
                        service_.sendMessage(message_field.getText().toString());
                        message_field.setText("");

                    }
                });
    }

    public void getNewMessages() {
//        return;
        SQLiteDatabase dbr = helper_.getReadableDatabase();

        SharedPreferences prefs = getSharedPreferences("omat", 0);
        int my_id = prefs.getInt("my_user_id", 0);
        Cursor cursor = dbr.query(false, "messages", new String[]{"message", "message_id", "from_id"}, "((from_id = ? and to_id = ?) or (from_id = ? and to_id = ?)) and message_id > ?", new String[] {
                Integer.toString(chat_partner_id_),
                Integer.toString(my_id),
                Integer.toString(my_id),
                Integer.toString(chat_partner_id_),
                Integer.toString(newest_message_id_)}, null, null, null, null);

        while (cursor.moveToNext()) {
            String this_message = cursor.getString(
                    cursor.getColumnIndexOrThrow("message")
            );
            int from_id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("from_id")
            );
            TextView tv = new TextView(ChatActivity.this);
            if( from_id == chat_partner_id_ ) {
                tv.setGravity(Gravity.RIGHT);
            }
            tv.setText(Integer.toString(from_id) + ": " + this_message);
            chatContainer_.addView(tv);

            newest_message_id_ = cursor.getInt(
                    cursor.getColumnIndexOrThrow("message_id")
            );
        }

        final ScrollView scroll = (ScrollView) findViewById(R.id.chatscroll);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    public class MessageResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "com.example.petri.MESSAGE_RECEIVED";

        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d("messageresponsereceiver", Integer.toString(newest_message_id_));
            if (intent.getIntExtra("from_id", 0) == chat_partner_id_ || intent.getIntExtra("from_id", 0) == getSharedPreferences("omat", 0).getInt("my_user_id", 0)) {
                getNewMessages();
            }


        }
    }

    public class NameResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "com.example.petri.NAME_RECEIVED";

        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d("messageresponsereceiver", Integer.toString(newest_message_id_));
           setTitle(intent.getStringExtra("name"));


        }
    }
}
