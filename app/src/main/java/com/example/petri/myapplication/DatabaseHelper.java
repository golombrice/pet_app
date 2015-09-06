package com.example.petri.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by petri on 21/08/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "test";

    private static final String MESSAGE_TABLE_CREATION =
            "CREATE TABLE messages(message_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, message TEXT, from_id INTEGER, to_id INTEGER)";
    private static final String CHAT_TABLE_CREATION =
            "CREATE TABLE chats(user_id INTEGER PRIMARY KEY NOT NULL, user_name TEXT, last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";


    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(MESSAGE_TABLE_CREATION);
        db.execSQL(CHAT_TABLE_CREATION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS chats");

        db.execSQL(MESSAGE_TABLE_CREATION);
        db.execSQL(CHAT_TABLE_CREATION);

    }
}

