package com.example.petri.myapplication;

import java.util.ArrayList;

/**
 * Created by petri on 27/08/15.
 */
public interface UserInfoListener {

    void receiverUserList(ArrayList< User > users);
    void receiveRegistrationInfo(int user_id);
    void receiveNotRegisteredNotification();
}
