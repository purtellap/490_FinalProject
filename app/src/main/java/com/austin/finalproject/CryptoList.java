package com.austin.finalproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.austin.finalproject.db.Favorite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoList implements Serializable {

    private static final boolean DEBUG = true;
    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "favs.txt";

    private JSONArray jsonArray;
    private ArrayList<Favorite> favorites;

    private FirebaseFirestore db;

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

    public JSONArray getFavoritesList(Context context){

        /*JSONArray favList = new JSONArray();

        String[] strFavs;

        try {
            InputStream inputStream = context.openFileInput(FILE_NAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                strFavs = stringBuilder.toString().split(".");

                inputStream.close();

                for (int i = 0; i < jsonArray.length(); i++){

                    JSONObject object = jsonArray.getJSONObject(i);
                    String id = object.getString("id");

                    if(Arrays.asList(strFavs).contains(id)) {
                        favList.put(object);
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        }
        catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }*/

        return jsonArray;
    }

    public void updateFavoritesList(Context c, String id){

        try {
            InputStream inputStream = c.openFileInput(FILE_NAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                List<String> favs = Arrays.asList(stringBuilder.toString().split("."));

                if(favs.contains(id)) {
                    favs.remove(id+".");
                }

                inputStream.close();
            }


            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(c.openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(id + ".");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        catch (Exception e){
            e.printStackTrace();

        }
    }
}

   /* private void dataBase(){

        db = FirebaseFirestore.getInstance();

        // Create a new user with a first, middle, and last name
        Map<String, Object> user = new HashMap<>();
        ArrayList<String> ligma = new ArrayList<>();
        ligma.add("balls");
        user.put("favorites", ligma);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void removeFromDB(String id){

    db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }*/
