package com.example.petri.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by petri on 19/08/15.
 */
public class User implements Parcelable {

    private int id_;
    private String name_;
    private String message_;

    User() {
        name_ = "";
        message_ = "";

        id_ = 0;
    }

    public void setName(String name) {
        name_ = name;
    }
    public void setMessage(String message) {
        message_ = message;
    }

    public void setId(int id) {
        id_ = id;
    }

    public int getId() {
        return id_;
    }

    public String getName() {
        return name_;
    }
    public String getMessage() {
        return message_;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name_);
        out.writeString(message_);

        out.writeInt(id_);
    }

    public static final Parcelable.Creator< User > CREATOR = new Parcelable.Creator< User >() {
        public User createFromParcel(Parcel in) {
            User out = new User();
            out.setName(in.readString());
            out.setMessage(in.readString());

            out.setId(in.readInt());
            return out;
        }
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
