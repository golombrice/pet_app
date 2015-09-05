package com.example.petri.myapplication;

import java.util.ArrayList;

/**
 * Created by petri on 27/08/15.
 */
public interface UserInfoListener {

    void receiverUserList(int request_id, ArrayList< User > users);
    void receiveRegistrationInfo(int request_id, int user_id);
    void receiveNotRegisteredNotification(int request_id);

}
