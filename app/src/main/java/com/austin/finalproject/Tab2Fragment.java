
package com.austin.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.austin.finalproject.db.Favorite;
import com.austin.finalproject.db.LabDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        Tab2Fragment.LoadCryptoListAsync reesync = new Tab2Fragment.LoadCryptoListAsync(view);
        reesync.execute();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();
        }
    }

    static class LoadCryptoListAsync extends AsyncTask<Void, Void, JSONArray> {

        private View view;

        LoadCryptoListAsync(View v){
            this.view = v;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {

            JSONArray triggeredArray = null;
            try {

                CryptoList cryptoList = new CryptoList();
                cryptoList.load();

                triggeredArray =  cryptoList.getCoinList();

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return triggeredArray;
        }

        @Override
        protected void onPostExecute(JSONArray allArray) {
            super.onPostExecute(allArray);

            if(allArray != null){
                Tab2Fragment.FavoritesAsync async = new Tab2Fragment.FavoritesAsync(view, view.getContext(), allArray, MainActivity.cryptoDB);
                async.execute();
            }
        }
    }

    static class FavoritesAsync extends AsyncTask<Void, Void, JSONArray> {

        private View view;
        private Context context;
        private LabDatabase database;
        private JSONArray array;

        FavoritesAsync(View v, Context c, JSONArray array , LabDatabase db){
            this.view = v;
            this.context = c;
            this.database = db;
            this.array = array;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {

            JSONArray favoritesArray = new JSONArray();
            try {

                ArrayList<Favorite> favs = (ArrayList<Favorite>) database.favoriteDao().getAllFavorites();

                ArrayList<String> favIDS = new ArrayList<>();
                for (int i = 0; i < favs.size(); i++){
                    favIDS.add(favs.get(i).getId());
                }

                Log.d("Favs", favIDS.toString());

                for (int i = 0; i < array.length(); i++){

                    JSONObject obj = array.getJSONObject(i);

                    for (int j = 0; j < favs.size(); j++){

                        if(obj.getString("id").equals(favs.get(j).getId())){
                            favoritesArray.put(obj);
                        }

                    }

                }

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return favoritesArray;
        }

        @Override
        protected void onPostExecute(JSONArray favoritesArray) {
            super.onPostExecute(favoritesArray);

            if(favoritesArray != null){

                RecyclerView recyclerView;
                RecyclerView.Adapter mAdapter;
                RecyclerView.LayoutManager layoutManager;
                recyclerView = view.findViewById(R.id.recycler_view2);

                // DISPLAYING THE LAYOUT
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);

                mAdapter = new RecyclerAdapter(favoritesArray);
                recyclerView.setAdapter(mAdapter);

                ProgressBar spinner = (ProgressBar)view.findViewById(R.id.progressBar2);
                spinner.setVisibility(View.GONE);
            }
        }

    }

}