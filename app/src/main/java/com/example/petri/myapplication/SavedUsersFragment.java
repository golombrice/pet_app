package com.example.petri.myapplication;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedUsersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavedUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavedUsersFragment newInstance(String param1, String param2) {
        SavedUsersFragment fragment = new SavedUsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SavedUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_users, container, false);


    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            DatabaseHelper helper = new DatabaseHelper(getActivity());
            SQLiteDatabase dbr = helper.getReadableDatabase();
            Cursor cursor = dbr.query(false, "chats", new String[]{"user_id", "user_name", "last_activity"}, null, null, null, null, "last_activity DESC", null);

            LinearLayout chat_container = (LinearLayout) getActivity().findViewById(R.id.saved_chats_container);

            chat_container.removeAllViews();
            while (cursor.moveToNext()) {
                String timestamp = cursor.getString(
                        cursor.getColumnIndexOrThrow("last_activity")
                );
                final int user_id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("user_id")
                );
                String user_name = cursor.getString(
                        cursor.getColumnIndexOrThrow("user_name")
                );
                TextView tv = new TextView(getActivity());

                tv.setText(user_name + timestamp);
                tv.setTextSize(25);
                tv.setOnClickListener(// Create an anonymous implementation of OnClickListener
                        new View.OnClickListener() {
                            public void onClick(View v) {
                                // do something when the button is clicked
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("chat_id", user_id);
                                startActivity(intent);
                            }
                        });
                chat_container.addView(tv);



            }
        }
    }


}
