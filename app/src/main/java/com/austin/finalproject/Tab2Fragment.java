
package com.austin.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.FileNotFoundException;

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        Tab2Fragment.FavoritesAsync async = new Tab2Fragment.FavoritesAsync(view, view.getContext());
        async.execute();

        return view;
    }

    static class FavoritesAsync extends AsyncTask<Void, Void, JSONArray> {

        private View view;
        private Context context;

        FavoritesAsync(View v, Context c){
            this.view = v;
            this.context = c;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {

            CryptoList cryptoList = new CryptoList();
            JSONArray favoritesArray = null;
            try {
                cryptoList.load();
                favoritesArray = cryptoList.getFavoritesList();
            }
            catch (FileNotFoundException f){
                Log.i("Failure", f.toString());
                return null;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return favoritesArray;
        }

        @Override
        protected void onPostExecute(JSONArray favoritesArray) {
            super.onPostExecute(favoritesArray);

            if(favoritesArray == null){
                Toast toast = Toast.makeText(context, "Error finding coins.", Toast.LENGTH_LONG);
                toast.show();
            }
            else {

                RecyclerView recyclerView;
                RecyclerView.Adapter mAdapter;
                RecyclerView.LayoutManager layoutManager;
                recyclerView = view.findViewById(R.id.recycler_view2);

                // DISPLAYING THE LAYOUT
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);

                mAdapter = new RecyclerAdapter(favoritesArray);
                recyclerView.setAdapter(mAdapter);
            }
        }

    }
}