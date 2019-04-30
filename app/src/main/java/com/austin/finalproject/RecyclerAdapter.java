package com.austin.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.austin.finalproject.db.Favorite;
import com.austin.finalproject.db.LabDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.austin.finalproject.MainActivity.cryptoDB;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private JSONArray coins;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout whateverLayout;
        TextView idText;
        TextView nameText;
        TextView symbolText;
        ToggleButton favButt;

        MyViewHolder(final LinearLayout layout) {
            super(layout);
            whateverLayout = layout;
            idText = (TextView) layout.findViewById(R.id.id);
            nameText = (TextView) layout.findViewById(R.id.coinName);
            symbolText = (TextView) layout.findViewById(R.id.coinSymbol);
            favButt = (ToggleButton) layout.findViewById(R.id.favbutton);

        }
    }

    RecyclerAdapter(JSONArray reeraw) {
        this.coins = reeraw;
    }

    @Override
    public @NonNull
    RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_list, parent, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        String name = "";
        String symbol = "";
        String id = "";

        try {

            JSONObject object = coins.getJSONObject(position);
            name = object.getString("name");
            symbol = object.getString("symbol");
            id = object.getString("id");

        }
        catch(Exception e){
            e.printStackTrace();
        }

        holder.idText.setText(id);
        holder.nameText.setText(name);
        holder.symbolText.setText(symbol);

        holder.whateverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SingleAsync async = new SingleAsync(holder.idText.getText().toString(), v, v.getContext());
                async.execute();

                System.out.println("you clicked on " + holder.symbolText.getText());
            }
        });

        checkForFavoriteAsync checkAsync = new checkForFavoriteAsync(holder,holder.idText.getText().toString(),MainActivity.cryptoDB);
        checkAsync.execute();


    }

    @Override
    public int getItemCount() {
        return coins.length();
    }

    static class checkForFavoriteAsync extends AsyncTask<Void, Void, Boolean> {

        private MyViewHolder holder;
        private String id;
        private LabDatabase db;

        checkForFavoriteAsync(MyViewHolder h, String id, LabDatabase db){
            this.holder = h;
            this.id = id;
            this.db = db;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                ArrayList<Favorite> favs = (ArrayList<Favorite>) db.favoriteDao().getAllFavorites();

                ArrayList<String> favIDS = new ArrayList<>();
                for (int i = 0; i < favs.size(); i++){
                    favIDS.add(favs.get(i).getId());
                }

                return favIDS.contains(id);

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isFavorite) {
            super.onPostExecute(isFavorite);

            if(isFavorite){
                holder.favButt.setChecked(true);
                holder.favButt.setBackgroundDrawable(ContextCompat.getDrawable(holder.whateverLayout.getContext(), R.drawable.ic_star_filled));
            }
            else{
                holder.favButt.setChecked(false);
                holder.favButt.setBackgroundDrawable(ContextCompat.getDrawable(holder.whateverLayout.getContext(), R.drawable.ic_star_border));
            }
            holder.favButt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {

                    if (isChecked){
                        buttonView.setBackgroundDrawable(ContextCompat.getDrawable(buttonView.getContext(),R.drawable.ic_star_filled));

                        // Inserts favorite into database
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    String id = holder.idText.getText().toString();
                                    Favorite fav = new Favorite(id);
                                    MainActivity.cryptoDB.favoriteDao().insertFavorite(fav);
                                }
                                catch ( android.database.sqlite.SQLiteConstraintException F){
                                    Log.d("Already existing id",id);
                                    F.printStackTrace();
                                }

                            }}).start();

                    }

                    else{
                        buttonView.setBackgroundDrawable(ContextCompat.getDrawable(buttonView.getContext(), R.drawable.ic_star_border));

                        // Removes favorite from database
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                String id = holder.idText.getText().toString();
                                Favorite fav = new Favorite(id);
                                MainActivity.cryptoDB.favoriteDao().removeFavorite(fav);
                            }}).start();
                    }
                }
            });
        }

    }

    static class SingleAsync extends AsyncTask<Void, Void, Crypto> {

        private View view;
        private Context context;
        private String id;

        SingleAsync(String s, View v, Context c){
            this.id = s;
            this.view = v;
            this.context = c;
            Log.d("id",id);
        }

        @Override
        protected Crypto doInBackground(Void... voids) {

            Crypto crypto = new Crypto(id);
            try {
                crypto.load();
            }
            catch (FileNotFoundException f){
                Log.i("Failure", f.toString());
                return null;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return crypto;
        }

        @Override
        protected void onPostExecute(Crypto crypto) {
            super.onPostExecute(crypto);

            if(crypto == null){
                Toast toast = Toast.makeText(context, "Error finding coin data.", Toast.LENGTH_LONG);
                toast.show();
            }
            else {

                // https://stackoverflow.com/questions/5944987/how-to-create-a-popup-window-popupwindow-in-android

                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);

                TextView popTitle = popupView.findViewById(R.id.pop_title);
                TextView popPrice = popupView.findViewById(R.id.pop_price);
                TextView popRank = popupView.findViewById(R.id.pop_rank);
                TextView popChange = popupView.findViewById(R.id.pop_change);
                TextView popMarket = popupView.findViewById(R.id.pop_market);

                popTitle.setText(crypto.getName());
                popPrice.setText(crypto.getPriceUSD());
                popRank.setText(crypto.getRank());
                popChange.setText(crypto.getChange24H());
                popMarket.setText(crypto.getMarketCap());

                if(crypto.getChange24H().startsWith("-") || crypto.getChange24H().equals("N/A")){
                    popChange.setTextColor(view.getResources().getColor(R.color.badRed));
                }
                else{
                    popChange.setTextColor(view.getResources().getColor(R.color.goodGreen));
                }

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        }

    }
}
