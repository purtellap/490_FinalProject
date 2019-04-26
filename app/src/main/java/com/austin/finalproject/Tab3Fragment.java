package com.austin.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.FileNotFoundException;

public class Tab3Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";

    static String query;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment,container,false);

        final SearchView searchView = (SearchView) view.findViewById(R.id.search);
        searchView.setImeOptions(searchView.getImeOptions()| EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        if(savedInstanceState != null){

            Tab3Fragment.SearchAsync async = new Tab3Fragment.SearchAsync(savedInstanceState.getString("query"), view, view.getContext());
            async.execute();
            searchView.setQuery(savedInstanceState.getString("query"), true);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Tab3Fragment.SearchAsync async = new Tab3Fragment.SearchAsync(s, searchView.getRootView(), getContext());
                async.execute();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                Tab3Fragment.query = s;
                Tab3Fragment.SearchAsync async = new Tab3Fragment.SearchAsync(s, searchView.getRootView(), getContext());
                async.execute();
                return false;
            }
        });

        return view;
    }

    static class SearchAsync extends AsyncTask<Void, Void, JSONArray> {

        private View view;
        private Context context;
        private String query;

        SearchAsync(String s, View v, Context c){
            this.query = s;
            this.view = v;
            this.context = c;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {

            CryptoList cryptoList = new CryptoList();
            JSONArray searchArray = null;
            try {
                cryptoList.load();
                searchArray = cryptoList.getSearchList(query);
            }
            catch (FileNotFoundException f){
                Log.i("Failure", f.toString());
                return null;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return searchArray;
        }

        @Override
        protected void onPostExecute(JSONArray searchArray) {
            super.onPostExecute(searchArray);

            if(searchArray == null){
                Toast toast = Toast.makeText(context, "Error finding coins.", Toast.LENGTH_LONG);
                toast.show();
            }
            else {

                RecyclerView recyclerView;
                RecyclerView.Adapter mAdapter;
                RecyclerView.LayoutManager layoutManager;
                recyclerView = view.findViewById(R.id.recycler_view3);

                // DISPLAYING THE LAYOUT
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);

                mAdapter = new RecyclerAdapter(searchArray);
                recyclerView.setAdapter(mAdapter);
            }
        }

    }
}