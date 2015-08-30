package com.example.petri.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by arkkaaja on 30/08/15.
 */
public final class Utilities {

    final static String SERVER_URL="http://80.222.146.25/~arkkaaja/";

    public static JSONObject sendGetRequest(String file, String params) {
        String urlStr = SERVER_URL + file + "?" + params;
        Log.d("sendGetRequest", urlStr);
        URL urlToRequest;
        HttpURLConnection urlConnection;
        try {
            urlToRequest = new URL(urlStr);
        } catch (java.net.MalformedURLException e) {
            return null;
        }
        try {
            urlConnection =
                    (HttpURLConnection) urlToRequest.openConnection();
        } catch (java.io.IOException e) {
            return null;
        }
        // handle issues
        int statusCode = 0;
        try {
            statusCode = urlConnection.getResponseCode();
        } catch (java.io.IOException e) {
            return null;
        }
        if (statusCode == HttpURLConnection.HTTP_OK) {
            Log.d("fds", "ok");
        }

        String stringresponse;
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;

            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            stringresponse = response.toString();
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        }
        try {
            return new JSONObject(stringresponse);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static int sendPostRequest(String file, JSONObject msg) {
        String urlStr = SERVER_URL + file;
        Log.d("sendPostRequest", urlStr);
        Log.d("sendPostRequest", msg.toString());

        URL urlToRequest;
        HttpURLConnection urlConnection;
        try {
            urlToRequest = new URL(urlStr);
        } catch (java.net.MalformedURLException e) {
            return 0;
        }
        try {
            urlConnection =
                    (HttpURLConnection) urlToRequest.openConnection();
        } catch (java.io.IOException e) {
            return 0;
        }
        urlConnection.setDoOutput(true);
        try {
            urlConnection.setRequestMethod("POST");
        } catch (java.net.ProtocolException e) {
            return 0;
        }
        urlConnection.setRequestProperty("Content-Type",
                "application/json");


        PrintWriter out;
        try {
            out = new PrintWriter(urlConnection.getOutputStream());
        } catch (java.io.IOException e) {
            return 0;
        }

        out.print(msg.toString());
        out.close();

        int statusCode = 0;
        try {
            statusCode = urlConnection.getResponseCode();
        } catch (java.io.IOException e) {
            return 0;
        }
        if (statusCode == HttpURLConnection.HTTP_OK) {
            Log.d("sendPostRequest", "ok");
        }
        return 1;
    }
}
