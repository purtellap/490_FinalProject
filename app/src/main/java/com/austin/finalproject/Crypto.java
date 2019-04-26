package com.austin.finalproject;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

public class Crypto implements Serializable {

    private static final boolean DEBUG = true;

    private static final String TAG_PREFIX = "ccTracker";

    private String id;
    private String name;
    private String symbol;
    private String rank;
    private String priceUSD;
    private String marketCap;
    private String change24H;


    public Crypto(String id)
    {
        this.id = id;

        if (DEBUG)
            Log.i(TAG_PREFIX + "Crypto()", "symbol = " + id);
    }


    public void load() throws java.io.IOException {

        URL url = new URL("https://api.coingecko.com/api/v3/coins/" + id);

        if (DEBUG)
            Log.i(TAG_PREFIX + "Stock.load()", "url = " + url);

        URLConnection connection = url.openConnection();

        if (DEBUG)
            Log.i(TAG_PREFIX + "Stock.load()", "url connection opened");

        InputStreamReader isr = new InputStreamReader((connection.getInputStream()));

        if (DEBUG)
            Log.i(TAG_PREFIX + "Stock.load()", "input stream reader created");

        BufferedReader in = new
                BufferedReader(isr);

        if (DEBUG)
            Log.i(TAG_PREFIX + "Stock.load()", "buffered reader created");

        String line = in.readLine();

        if (DEBUG)
            Log.i(TAG_PREFIX + "Stock.load()", "line = " + line);


        // consume any data remaining in the input stream
        while (in.readLine() != null)
            ;

        in.close();

        if (line != null && line.length() > 0)
        {
            // parse the JSON
            try {
                JSONObject crypto_full = new JSONObject(line);
                JSONObject marketData = crypto_full.getJSONObject("market_data");
                JSONObject currentPrice = marketData.getJSONObject("current_price");
                name = crypto_full.getString("name");
                priceUSD = Double.toString(currentPrice.getDouble("usd"));
                change24H = marketData.getString("price_change_percentage_24h");
                rank = marketData.getString("market_cap_rank");
                marketCap = marketData.getJSONObject("market_cap").getString("usd");
            }
            catch (Exception e){
                Log.e(TAG_PREFIX, "Error retrieving data from JSON");
            }

            if (DEBUG)
                Log.i(TAG_PREFIX + "Crypto.load()", "name = " + name);

        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPriceUSD() {
        return "$" + priceUSD;
    }

    public String getMarketCap() {
        return "$" + marketCap;
    }

    public String getChange24H() {
        if(change24H.equals("null")){
            return "N/A";
        }
        return change24H + "%";
    }

    public String getRank() {
        return "#" + rank;
    }
}
