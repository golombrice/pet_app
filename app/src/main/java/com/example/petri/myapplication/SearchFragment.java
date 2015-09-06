package com.example.petri.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements UserInfoListener, serviceConnectionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HashSet<Integer> pending_requests_ = new HashSet<>();

    private MainService service_;
    private boolean bound_ = false;

    private boolean users_fetched_ = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }


//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        pending_requests_.add(service_.getUsers());
//    }

    public void show_users(ArrayList<User> users_parc) {
        LinearLayout user_list = (LinearLayout) getActivity().findViewById(R.id.userList);

        for (int i = 0; i < users_parc.size(); ++i) {
            Log.d("activ", Integer.toString(users_parc.get(i).getId()) + " " + users_parc.get(i).getName());
            UserView name_field = new UserView(getActivity());
//            TextView name_field = new TextView(MainActivity.this);
            name_field.setName(users_parc.get(i).getName());
            name_field.setMessage(users_parc.get(i).getMessage());
//            name_field.setTextSize(25);
            final int user_id = users_parc.get(i).getId();
            final String user_name = users_parc.get(i).getName();

            name_field.setOnClickListener(// Create an anonymous implementation of OnClickListener
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            // do something when the button is clicked
                            addChatToDatabase(user_id, user_name);
                            UserView tv = (UserView) v;
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("chat_id", user_id);
                            startActivity(intent);
                        }
                    });
            user_list.addView(name_field);

        }
    }

    private void addChatToDatabase(int user_id, String user_name) {

        DatabaseHelper helper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("user_name", user_name);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId=db.insert(
                "chats",
                null,
                values);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void receiverUserList(int request_id, ArrayList<User> users) {
        if ( !pending_requests_.contains(request_id) ) {
            return;
        }

        pending_requests_.remove(request_id);

        show_users(users);
        users_fetched_ = true;



    }

    @Override
    public void receiveRegistrationInfo(int request_id, int user_id) {

    }

    @Override
    public void receiveNotRegisteredNotification(int request_id) {

    }

    @Override
    public void onConnected(MainService service) {
        service_ = service;
        bound_ = true;
        service_.registerListener(this);

        if( !users_fetched_ ) {
            pending_requests_.add(service_.getUsers());
        }
    }

    @Override
    public void onDisconnected(ComponentName name) {
        bound_ = false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
