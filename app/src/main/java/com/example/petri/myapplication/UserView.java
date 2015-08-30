package com.example.petri.myapplication;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by arkkaaja on 29/08/15.
 */
public class UserView extends LinearLayout {
    TextView name_field_;
    TextView message_field_;

    public UserView(Context context) {
        super(context);

        inflate(context, R.layout.user_view, this);

        name_field_ = (TextView)findViewById(R.id.name_field);
        message_field_ = (TextView)findViewById(R.id.comment_field);

//        this.setOrientation(VERTICAL);
//        this.setBackground(getResources().getDrawable(R.drawable.border));

//         name_field_ = new TextView(context);
//        name_field_.setTextSize(25);
//        this.addView(name_field_);
//
//        message_field_ = new TextView(context);
//        this.addView(message_field_);
    }

    public void setName(String text) {
        name_field_.setText(text);
    }
    public void setMessage(String text) {
        message_field_.setText(text);
    }
//    public void setId(String text) {
//        name_field_.setText(text);
//    }
}
