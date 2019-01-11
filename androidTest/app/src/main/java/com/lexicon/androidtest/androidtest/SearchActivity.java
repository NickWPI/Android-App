package com.lexicon.androidtest.androidtest;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    ListView m_ListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d("SEARCH", "search activity created");

        m_ListView = findViewById(R.id.list_view);
        m_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override public void onItemClick(AdapterView<?> parent, View view,
                                              int position, long id) {
                Toast.makeText(SearchActivity.this,
                        "clicked search result item is "+((TextView)view).getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        handleIntent(getIntent());
    }

    @Override
    public boolean onSearchRequested() {
        Log.d("SEARCH", "search triggered");
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //handleSearch(query);
        }
        else if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            String dataString =  intent.getDataString();
            handleView(dataString);
            //Toast.makeText(this, "selected search suggestion " + dataString,
            //        Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSearch(String query) {
        //SearchListAdapter adapter = new SearchListAdapter(this,
        //        android.R.layout.simple_dropdown_item_1line, tempList);
        //m_ListView.setAdapter(adapter);
        //this.finish();
    }

    private void handleView(String row) {
        String[] strings = row.split(" ");
        String name = strings[0];
        String symbol = strings[1];
        Intent intent = new Intent(this, DisplayDetailsFragment.class);
        intent.putExtra("Name", name);
        intent.putExtra("Symbol", symbol);
        //this needs to be optimized, pretty inefficient
        String[] symbols = { symbol };
        List<Cryptocurrency> crypto = CryptocurrencyInfo.getInstance().getPrice(symbols);
        Cryptocurrency cryptocurrency = crypto.get(0);
        intent.putExtra("Price", cryptocurrency.getPrice());
        intent.putExtra("PercentChange", cryptocurrency.getPercentageGained24h());
        intent.putExtra("PriceChange", cryptocurrency.getPriceChange());
        this.startActivity(intent);
        this.finish();
    }
}
