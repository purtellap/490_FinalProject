package com.austin.finalproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CryptoList implements Serializable {

    private static final boolean DEBUG = true;

    private JSONArray jsonArray;

    public void load() throws java.io.IOException {

        URL url = new URL("https://api.coingecko.com/api/v3/coins/list");

        if (DEBUG)
            Log.i("CryptoList.load()", "url = " + url);

        URLConnection connection = url.openConnection();

        if (DEBUG)
            Log.i("CryptoList.load()", "url connection opened");

        InputStreamReader isr = new InputStreamReader((connection.getInputStream()));

        if (DEBUG)
            Log.i("CryptoList.load()", "input stream reader created");

        BufferedReader in = new
                BufferedReader(isr);

        if (DEBUG)
            Log.i("CryptoList.load()", "buffered reader created");

        String line = in.readLine();

        if (DEBUG)
            Log.i("CryptoList.load()", "line = " + line);


        // consume any data remaining in the input stream
        while (in.readLine() != null)
            ;

        in.close();

        if (line != null && line.length() > 0)
        {

            System.out.print(line);

            try {
                jsonArray = new JSONArray(line);

            } catch (Exception ex) {
                Log.e("JSON", "Error parsing JSON");
            }

        }
    }

    public JSONArray getCoinList() {
        return jsonArray;
    }

    public JSONArray getSearchList(String query){

        JSONArray searchList = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++){

            try {
                JSONObject object = jsonArray.getJSONObject(i);
                String name = object.getString("name");
                String symbol = object.getString("symbol");
                String id = object.getString("id");

                if(name.contains(query) || symbol.contains(query) || id.contains(query)) {
                    searchList.put(object);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return searchList;

    }

    public JSONArray getFavoritesList(){

        return jsonArray;
    }
}
