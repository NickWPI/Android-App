package com.lexicon.androidtest.androidtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.ArraySet;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class CryptocurrencyInfo {
    CryptocurrencyDataset m_Dataset;
    CryptocurrencyDataset m_OldDataset;
    //WASTING TOO MUCH MEMORY, USE ANOTHER CLASS TO HOLD ALL THIS DATA
    HashMap<Integer, Map.Entry<String, String>> m_CryptocurrencyNames;
    HashMap<String, String> m_CryptocurrencyIds;
    HashMap<String, HashMap<String, String>> m_CryptocurrencyStats;
    HashMap<String, Bitmap> m_CryptocurrencyImages;
    HashMap<String, Cryptocurrency> m_PreviousPrices;
    HashMap<String, Set<String>> m_SupportedExchanges;
    ArrayList<String> m_ExchangeList;
    HashMap<String, Set<String>> m_TradingPairs;
    HashMap<String, String> m_DefaultTradingPairs;
    ArrayList<Map.Entry<String, String>> m_TopList;

    public static class TradingPair {
        public String m_Exchange;
        public String m_Coin;
        public Set<String> m_TradingPairs;
    }

    public static class NewsArticle {
        public String m_Guid;
        public String m_ImageUrl;
        public String m_Date;
        public String m_Title;
        public String m_Url;
        public String m_Source;
        public String m_Body;
        public String m_Tags;
    }

    ArrayList<NewsArticle> m_NewsArticles;

    public CryptocurrencyInfo() {
        m_CryptocurrencyNames = new HashMap<Integer, Map.Entry<String, String>>();
        m_CryptocurrencyIds = new HashMap<String, String>();
        m_Dataset = new CryptocurrencyDataset();
        m_OldDataset = new CryptocurrencyDataset();
        m_CryptocurrencyStats = new HashMap<String, HashMap<String, String>>();
        m_CryptocurrencyImages = new HashMap<String, Bitmap>();
        m_PreviousPrices = new HashMap<String, Cryptocurrency>();
        m_SupportedExchanges = new HashMap<>();
        m_ExchangeList = new ArrayList<>();
        m_TradingPairs = new HashMap<String, Set<String>>();
        m_DefaultTradingPairs = new HashMap<String, String>();
        m_TopList = new ArrayList<>();
        m_NewsArticles = new ArrayList<>();
    }

    public CryptocurrencyDataset getDataset() {
        return m_Dataset;
    }

    public CryptocurrencyDataset getOldDataset() {
        return m_OldDataset;
    }

    public HashMap<Integer, Map.Entry<String, String>> getCryptocurrencyNames() {
        return m_CryptocurrencyNames;
    }

    public HashMap<String, Set<String>> getTradingPairs() {
        return m_TradingPairs;
    }

    public Set<String> getTradingPairs(String name) {
        return m_TradingPairs.get(name);
    }

    public ArrayList<Map.Entry<String, String>> getTopList() {
        return m_TopList;
    }

    private String performGetRequest(String urlString) {
        final StringBuilder[] stringBuilder = new StringBuilder[1];
        final URL url;
        try {
            url = new URL(urlString);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    HttpsURLConnection connection = null;
                    try {
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = br.readLine()) != null){
                            sb.append(line + "\n");
                        }
                        br.close();
                        stringBuilder[0] = sb;
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
            thread.join();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return stringBuilder[0].toString();
    }

    public void generateCryptoDisplayList() {
        String result = performGetRequest("https://min-api.cryptocompare.com/data/top/mktcapfull?limit=100&tsym=USD");
        try {
            JSONObject jObject = new JSONObject(result.trim());
            JSONArray data = jObject.getJSONArray("Data");
            for(int i = 0; i < data.length(); i++) {
                JSONObject object = (JSONObject) data.get(i);
                JSONObject coinInfo = object.getJSONObject("CoinInfo");
                String symbol = coinInfo.getString("Name");
                String name = coinInfo.getString("FullName");
                JSONObject raw = object.getJSONObject("RAW");
                JSONObject usd = raw.getJSONObject("USD");
                String volume = usd.getString("VOLUME24HOUR");
                if(Double.parseDouble(volume) == 0) {
                    continue;
                }
                m_TopList.add(new HashMap.SimpleEntry<>(name, symbol));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void generateCryptocurrencyList() {
        String result = performGetRequest("https://min-api.cryptocompare.com/data/all/coinlist");
        try {
            JSONObject jObject = new JSONObject(result.trim());
            JSONObject data = jObject.getJSONObject("Data");
            boolean started = false;
            Iterator<?> keys = data.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if (data.get(key) instanceof JSONObject ) {
                    JSONObject obj = (JSONObject)data.get(key);
                    String name = obj.getString("CoinName");
                    String symbol = obj.getString("Symbol");
                    String order = obj.getString("SortOrder");
                    String id = obj.getString("Id");
                    if(name.contains("Bitcoin"))
                        started = true;
                    if(!started)
                        continue;
                    Cryptocurrency cryptocurrency = new Cryptocurrency(name, symbol);
                    m_Dataset.addCryptocurrency(Integer.valueOf(order), cryptocurrency);
                    m_CryptocurrencyNames.put(Integer.valueOf(order), new HashMap.SimpleEntry<String, String>(name, symbol));
                    m_CryptocurrencyIds.put(symbol, id);
                }
            }

            Object[] entrySet = m_CryptocurrencyNames.entrySet().toArray();
            Arrays.sort(entrySet, new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    return ((Map.Entry<Integer, Map.Entry<String,String>>) o).getKey()
                            .compareTo(((Map.Entry<Integer, Map.Entry<String,String>>) t1).getKey());
                }
            });
            HashMap<Integer, Map.Entry<String,String>> newMap = new HashMap<Integer, Map.Entry<String,String>>();
            int count = 0;
            for(Object o : entrySet) {
                newMap.put(count, ((Map.Entry<Integer, Map.Entry<String,String>>)o).getValue());
                //Log.d("CRYPTONAMES", count + " -> " + ((Map.Entry<Integer, Map.Entry<String,String>>)o).getValue());
                count++;
            }
            m_CryptocurrencyNames = newMap;

            m_Dataset.adjustDataset();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void generateExchangeList() {
        String result = performGetRequest("https://min-api.cryptocompare.com/data/all/exchanges");
        try {
            JSONObject data = new JSONObject(result.trim());
            Iterator<?> keys = data.keys();
            m_ExchangeList.add("CCCAGG");
            while(keys.hasNext()) {
                String key = (String) keys.next();
                String exchange = key;
                m_ExchangeList.add(exchange);
                JSONObject obj = (JSONObject) data.get(key);
                Iterator<?> coins = obj.keys();
                while(coins.hasNext()) {
                    String coin = (String) coins.next();
                    JSONArray coinObject = (JSONArray) obj.get(coin);
                    for(int i = 0; i < coinObject.length(); i++) {
                        String pair = (String) coinObject.get(i);
                        if(m_TradingPairs.containsKey(coin)) {
                            Set<String> pairs = m_TradingPairs.get(coin);
                            pairs.add(coin + "-" + pair);
                        }
                        else {
                            Set<String> pairs = new ArraySet<>();
                            pairs.add(coin + "-" + pair);
                            m_TradingPairs.put(coin, pairs);
                        }
                        String tradeString = coin + "-" + pair;
                        if(m_SupportedExchanges.containsKey(tradeString)) {
                            Set<String> exchanges = m_SupportedExchanges.get(tradeString);
                            exchanges.add(exchange);
                        }
                        else {
                            Set<String> exchanges = new ArraySet<>();
                            exchanges.add("CCCAGG");
                            exchanges.add(exchange);
                            m_SupportedExchanges.put(tradeString, exchanges);
                        }
                    }
                    Set<String> pairs = m_TradingPairs.get(coin);
                    String defaultPair = generateDefaultTradingPair(pairs);
                    m_DefaultTradingPairs.put(coin, defaultPair);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String generateDefaultTradingPair(Set<String> pairs) {
        String[] defaultPairList = {"USD", "USDT", "BTC"};
        for(String df : defaultPairList) {
            for (String pair : pairs) {
                String[] s = pair.split("-");
                if (s[1].equals(df)) {
                    return pair;
                }
            }
        }
        return "";
    }

    public Set<String> getSupportedExchanges(String tradeString) {
        return m_SupportedExchanges.get(tradeString);
    }

    public List<String> getExchangeList() {
        return m_ExchangeList;
    }

    public String getDefaultTradingPair(String coin) {
        return m_DefaultTradingPairs.get(coin);
    }

    public HashMap<String, String> getDefaultTradingPairs() {
        return m_DefaultTradingPairs;
    }

    public HashMap<String, Cryptocurrency> getPreviousPriceList() {
        return m_PreviousPrices;
    }

    public Cryptocurrency getPrice(String tradingString, String exchange) {
        String[] pair = tradingString.split("-");
        String fromCurrency = pair[0];
        String toCurrency = "USD";
        if(pair.length > 1) {
            toCurrency = pair[1];
        }
        String url = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=" + fromCurrency + "&tsyms=" + toCurrency + "&e=" + exchange;
        String result = performGetRequest(url);
        Cryptocurrency cryptocurrency = null;
        try {
            JSONObject jObject = new JSONObject(result.trim());
            JSONObject dataObject = jObject.getJSONObject("RAW");
            Iterator<String> iterator = dataObject.keys();
            while(iterator.hasNext()) {
                String key = iterator.next();
                if(dataObject.get(key) instanceof JSONObject) {
                    String coinSymbol = key;
                    JSONObject coinObj = (JSONObject)dataObject.get(key);
                    JSONObject priceObj = (JSONObject)coinObj.get(toCurrency);
                    String price = priceObj.getString("PRICE");
                    DecimalFormat df2 = new DecimalFormat("#.##");
                    df2.setMinimumFractionDigits(2);
                    String percentChange24h = df2.format(priceObj.getDouble("CHANGEPCT24HOUR")) + "%";
                    Double exactChange = priceObj.getDouble("CHANGE24HOUR");
                    String change = "";
                    DecimalFormat df = new DecimalFormat();
                    if(exactChange < 0.01) {
                        df.applyPattern("#.####");
                        df.setMinimumFractionDigits(4);
                        change = df.format(exactChange);
                    } else {
                        df.applyPattern("#.##");
                        df.setMinimumFractionDigits(2);
                        change = df.format(exactChange);
                    }
                    Cryptocurrency c = m_Dataset.getCryptocurrencyBySymbol(coinSymbol);
                    cryptocurrency = new Cryptocurrency(c.getName(), tradingString);
                    cryptocurrency.setDataset(price, change, "0", percentChange24h, "0");
                    m_PreviousPrices.put(tradingString, cryptocurrency);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cryptocurrency;
    }

    public Cryptocurrency getPrice(String tradingString) {
        return getPrice(tradingString, "CCCAGG");
    }

    public List<Cryptocurrency> getPrice(String[] currencies) {
        if(currencies.length == 0)
            return null;
        ArrayList<Cryptocurrency> cryptocurrencyList = new ArrayList<Cryptocurrency>();
        String currencyString = "";
        for(String c : currencies) {
            currencyString = currencyString + c + ",";
        }
        //shave off the last comma
        currencyString = currencyString.substring(0, currencyString.length() - 1);
        Log.d("DEBUG", "looking up price for: " + currencyString);
        String url = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=" + currencyString + "&tsyms=USD";
        String result = performGetRequest(url);
        try {
            JSONObject jObject = new JSONObject(result.trim());
            JSONObject dataObject = jObject.getJSONObject("RAW");
            Iterator<String> iterator = dataObject.keys();
            while(iterator.hasNext()) {
                String key = iterator.next();
                if(dataObject.get(key) instanceof JSONObject) {
                    String coinSymbol = key;
                    JSONObject coinObj = (JSONObject)dataObject.get(key);
                    JSONObject priceObj = (JSONObject)coinObj.get("USD");
                    String price = priceObj.getString("PRICE");
                    DecimalFormat df2 = new DecimalFormat("#.##");
                    df2.setMinimumFractionDigits(2);
                    String percentChange24h = df2.format(priceObj.getDouble("CHANGEPCT24HOUR")) + "%";
                    Double exactChange = priceObj.getDouble("CHANGE24HOUR");
                    String change = "";
                    DecimalFormat df = new DecimalFormat();
                    if(exactChange < 0.01) {
                        df.applyPattern("#.####");
                        df.setMinimumFractionDigits(4);
                        change = df.format(exactChange);
                    } else {
                        df.applyPattern("#.##");
                        df.setMinimumFractionDigits(2);
                        change = df.format(exactChange);
                    }
                    //String price = coinObj.getString("USD");
                    Cryptocurrency cryptocurrency = m_Dataset.getCryptocurrencyBySymbol(coinSymbol);
                    cryptocurrency.setDataset(price, change, "0", percentChange24h, "0");
                    cryptocurrencyList.add(cryptocurrency);
                    m_PreviousPrices.put(cryptocurrency.getName(), cryptocurrency);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cryptocurrencyList;
    }

    public List<Cryptocurrency> getPrice(String[] fromCurrencies, String[] toCurrencies) {
        if(toCurrencies.length == 0 || fromCurrencies.length == 0) {
            return null;
        }
        ArrayList<Cryptocurrency> cryptocurrencyList = new ArrayList<Cryptocurrency>();
        String toCurrencyString = "";
        for(String c : toCurrencies) {
            toCurrencyString = toCurrencyString + c + ",";
        }
        toCurrencyString = toCurrencyString.substring(0, toCurrencyString.length() - 1);
        String fromCurrencyString = "";
        for(String c : fromCurrencies) {
            fromCurrencyString = fromCurrencyString + c + ",";
        }
        fromCurrencyString = fromCurrencyString.substring(0, fromCurrencyString.length() - 1);

        String url = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=" + fromCurrencyString + "&tsyms=" + toCurrencyString;
        String result = performGetRequest(url);
        try {
            JSONObject jObject = new JSONObject(result.trim());
            JSONObject dataObject = jObject.getJSONObject("RAW");
            Iterator<String> iterator = dataObject.keys();
            while(iterator.hasNext()) {
                String key = iterator.next();
                if(dataObject.get(key) instanceof JSONObject) {
                    String coinSymbol = key;
                    JSONObject coinObj = (JSONObject)dataObject.get(key);
                    for(String toCurrency : toCurrencies) {
                        JSONObject toCurrencyObj = (JSONObject)coinObj.get(toCurrency);
                        String price = toCurrencyObj.getString("PRICE");
                        String tradingString = coinSymbol + "-" + toCurrency;

                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setMinimumFractionDigits(2);
                        String percentChange24h = df.format(toCurrencyObj.getDouble("CHANGEPCT24HOUR")) + "%";
                        Double exactChange = toCurrencyObj.getDouble("CHANGE24HOUR");
                        String change = "";
                        DecimalFormat df2 = new DecimalFormat();
                        if(exactChange < 0.01) {
                            df2.applyPattern("#.####");
                            df2.setMinimumFractionDigits(4);
                            change = df.format(exactChange);
                        } else {
                            df2.applyPattern("#.##");
                            df2.setMinimumFractionDigits(2);
                            change = df2.format(exactChange);
                        }

                        Cryptocurrency c = m_Dataset.getCryptocurrencyBySymbol(coinSymbol);
                        Cryptocurrency cryptocurrency = new Cryptocurrency(c.getName(), tradingString);
                        cryptocurrency.setDataset(price, change, "0", percentChange24h, "0");
                        cryptocurrencyList.add(cryptocurrency);
                        m_PreviousPrices.put(tradingString, cryptocurrency);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cryptocurrencyList;
    }

    public List<Cryptocurrency> getPriceByTradeStrings(String[] tradeStrings) {
        Set<String> fromCurrencies = new ArraySet<String>();
        Set<String> toCurrencies = new ArraySet<String>();
        for(String s : tradeStrings) {
            String[] trade = s.split("-");
            fromCurrencies.add(trade[0]);
            if(trade.length == 1) {
                toCurrencies.add("USD");
            }
            else {
                toCurrencies.add(trade[1]);
            }
        }

        return getPrice(fromCurrencies.toArray(new String[0]), toCurrencies.toArray(new String[0]));
    }

    public HashMap<String, String> getStatistics(String symbol) {
        if(m_CryptocurrencyStats.containsKey(symbol)) {
            return m_CryptocurrencyStats.get(symbol);
        }
        String id = m_CryptocurrencyIds.get(symbol);
        String url = "https://www.cryptocompare.com/api/data/coinsnapshotfullbyid/?id=" + id;
        String result = performGetRequest(url);
        HashMap<String, String> stats = new HashMap<String, String>();
        try {
            JSONObject jObject = new JSONObject(result.trim());
            JSONObject data = jObject.getJSONObject("Data");
            JSONObject general = data.getJSONObject("General");
            String imageUrl = "https://www.cryptocompare.com" + general.getString("ImageUrl");
            String totalSupply = general.getString("TotalCoinSupply");
            String algorithm = general.getString("Algorithm");
            String proofType = general.getString("ProofType");
            String startDate = general.getString("StartDate");
            String blockNumber = general.getString("BlockNumber");
            String blockTime = general.getString("BlockTime");
            String netHashesPerSecond = general.getString("NetHashesPerSecond");
            String totalCoinsMined = general.getString("TotalCoinsMined");
            String blockReward = general.getString("BlockReward");
            stats.put("ImageUrl", imageUrl);
            stats.put("TotalCoinSupply", totalSupply);
            stats.put("Algorithm", algorithm);
            stats.put("ProofType", proofType);
            stats.put("StartDate", startDate);
            stats.put("BlockNumber", blockNumber);
            stats.put("BlockTime", blockTime);
            stats.put("NetHashesPerSecond", netHashesPerSecond);
            stats.put("TotalCoinsMined", totalCoinsMined);
            stats.put("BlockReward", blockReward);
            m_CryptocurrencyStats.put(symbol, stats);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public Bitmap loadImage(final String symbol) {
        HashMap<String, String> stats = CryptocurrencyInfo.getInstance().getStatistics(symbol);
        final String urlString = stats.get("ImageUrl");
        final URL url;
        try {
            url = new URL(urlString);
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                m_CryptocurrencyImages.put(symbol, bitmap);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface OnLoadImageListener {
        void onLoad(String symbol, Bitmap bitmap);
    }

    public void loadImageAsync(final String symbol, final OnLoadImageListener listener) {
        HashMap<String, String> stats = CryptocurrencyInfo.getInstance().getStatistics(symbol);
        final String urlString = stats.get("ImageUrl");
        final URL url;
        final Bitmap bitmap = null;
        try {
            url = new URL(urlString);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    if(m_CryptocurrencyImages.containsKey(symbol)) {
                        Bitmap bitmap = m_CryptocurrencyImages.get(symbol);
                        listener.onLoad(symbol, bitmap);
                        return;
                    }
                    HttpsURLConnection connection = null;
                    try {
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        m_CryptocurrencyImages.put(symbol, bitmap);
                        if(listener != null)
                            listener.onLoad(symbol, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void loadImagesAsync(final String[] symbols, final OnLoadImageListener listener) {
        for(String symbol : symbols) {

        }
    }

    public Bitmap getImage(String symbol) {
        if(isImageLoaded(symbol)) {
            return m_CryptocurrencyImages.get(symbol);
        }
        return null;
    }

    public boolean isImageLoaded(String symbol) {
        return m_CryptocurrencyImages.containsKey(symbol);
    }

    public void generateNewsArticles() {
        String result = performGetRequest("https://min-api.cryptocompare.com/data/v2/news/?lang=EN");
        try {
            JSONObject jObject = new JSONObject(result.trim());
            JSONArray data = jObject.getJSONArray("Data");
            for(int i = 0; i < data.length(); i++) {
                JSONObject newsObj = data.getJSONObject(i);
                NewsArticle article = new NewsArticle();
                article.m_Guid = newsObj.getString("guid");
                article.m_ImageUrl = newsObj.getString("imageurl");
                article.m_Date = newsObj.getString("published_on");
                article.m_Title = newsObj.getString("title");
                article.m_Url = newsObj.getString("url");
                article.m_Source = newsObj.getString("source");
                article.m_Body = newsObj.getString("body");
                article.m_Tags = newsObj.getString("tags");
                m_NewsArticles.add(article);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<NewsArticle> getNewsArticles() {
        return m_NewsArticles;
    }

    public static class CryptocurrencyResponseData {
        public String name;
        public String price;
        public String lastVolume;
        public String volume;
        public String percentageGained24h;
    }

    public CryptocurrencyResponseData[] getMultiCryptocurrencyData(String[] currencies, String[] markets) {
        return null;
    }

    private static class Holder {
        private static final CryptocurrencyInfo instance = new CryptocurrencyInfo();
    }

    public static CryptocurrencyInfo getInstance() {
        return Holder.instance;
    }
}
