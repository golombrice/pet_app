package com.example.petri.myapplication;

import android.content.ComponentName;

/**
 * Created by arkkaaja on 05/09/15.
 */
public interface serviceConnectionListener {
     void onConnected(MainService service);
     void onDisconnected(ComponentName name);
}
