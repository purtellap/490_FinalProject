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

import java.io.FileNotFoundException;

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        AllAsync async = new AllAsync(view, getContext());
        async.execute();

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

    static class AllAsync extends AsyncTask<Void, Void, CryptoList> {

        private View view;
        private Context context;

        AllAsync(View v, Context c){
            this.view = v;
            this.context = c;
        }

        @Override
        protected CryptoList doInBackground(Void... voids) {

            CryptoList cryptoList = new CryptoList();
            try {
                cryptoList.load();
            }
            catch (FileNotFoundException f){
                Log.i("Failure", f.toString());
                return null;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return cryptoList;
        }

        @Override
        protected void onPostExecute(CryptoList cryptoList) {
            super.onPostExecute(cryptoList);

            if(cryptoList == null){
                Toast toast = Toast.makeText(context, "Error finding coins.", Toast.LENGTH_LONG);
                toast.show();
            }
            else {

                RecyclerView recyclerView;
                RecyclerView.Adapter mAdapter;
                RecyclerView.LayoutManager layoutManager;
                recyclerView = view.findViewById(R.id.recycler_view);

                // DISPLAYING THE LAYOUT
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);

                mAdapter = new RecyclerAdapter(cryptoList.getCoinList());
                recyclerView.setAdapter(mAdapter);

                ProgressBar spinner = (ProgressBar)view.findViewById(R.id.progressBar);
                spinner.setVisibility(View.GONE);
            }
        }

    }
}