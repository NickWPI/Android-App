package com.lexicon.androidtest.androidtest;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SearchContentProvider extends ContentProvider {
    private static final String CRYPTOCURRENCIES = "cryptocurrencies/"+ SearchManager.SUGGEST_URI_PATH_QUERY+"/*";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI("com.lexicon.androidtest.search", CRYPTOCURRENCIES, 1);
    }

    private static String[] matrixCursorColumns = { "_id",
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            //SearchManager.SUGGEST_COLUMN_ICON_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA};

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        switch(uriMatcher.match(uri)){
            case 1:
                String query = uri.getLastPathSegment().toLowerCase();
                return getSearchResultsCursor(query);
            default:
                return null;
        }
    }

    private MatrixCursor getSearchResultsCursor(String searchString) {
        MatrixCursor searchResults =  new MatrixCursor(matrixCursorColumns);
        Object[] mRow = new Object[3];
        int counterId = 0;
        if(searchString != null) {
            searchString = searchString.toLowerCase();
            HashMap<Integer, Map.Entry<String, String>> cryptocurrencies = CryptocurrencyInfo.getInstance().getCryptocurrencyNames();
            Collection<Map.Entry<String, String>> entries = cryptocurrencies.values();
            for(Map.Entry<String, String> entry : entries){
                String name = entry.getKey();
                String symbol = entry.getValue();
                if(name.toLowerCase().contains(searchString)
                        || symbol.toLowerCase().contains(searchString)){
                    mRow[0] = "" + counterId++;
                    mRow[1] = name + " (" + symbol + ")";
                    //mRow[2] = Uri.parse("https://www.cryptocompare.com/media/19633/btc.png");
                    mRow[2] = "" + name + " " + symbol;
                    searchResults.addRow(mRow);
                }
            }
        }
        return searchResults;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
