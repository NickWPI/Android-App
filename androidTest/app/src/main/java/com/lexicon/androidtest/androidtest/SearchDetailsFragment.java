package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchDetailsFragment extends Fragment {
    private RecyclerView m_RecyclerView;
    private SearchRecyclerViewAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    OnLaunchFragmentListener m_FragmentListener;
    OnSearchDetailsListener m_OnSearchDetailsListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            m_FragmentListener = (OnLaunchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLaunchFragmentListener");
        }
    }

    private Fragment findFragment(FragmentManager manager, String name) {
        Fragment fragment = manager.findFragmentByTag(name);
        if(fragment == null) {
            for(Fragment f : manager.getFragments()) {
                fragment = findFragment(f.getChildFragmentManager(), name);
                if(fragment != null)
                    break;
            }
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_details, parent, false);
        setHasOptionsMenu(true);

        Bundle args = this.getArguments();
        String name = args.getString("FragmentName");

        try {
            //FragmentManager manager = this.getActivity().getSupportFragmentManager();
            Fragment fragment = FragmentRepository.getInstance().getFragment(name);
            /*if(fragment == null) {
                //there needs to be a replacement for this...
                //pass values through this class' constructor instead
                fragment = findFragment(manager, name);
            }*/
            m_OnSearchDetailsListener = (OnSearchDetailsListener) fragment;
        } catch(ClassCastException e) {
            throw new ClassCastException(name + " must implement OnSearchDetailsListener");
        }

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity)this.getActivity();
        activity.setSupportActionBar(myToolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        m_RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this.getContext());
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        m_Adapter = new SearchRecyclerViewAdapter();
        m_Adapter.setFragment(this);
        m_Adapter.setOnLaunchFragmentListener(m_FragmentListener);
        m_Adapter.setOnSearchDetailsListener(m_OnSearchDetailsListener);
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(5));
        m_RecyclerView.setAdapter(m_Adapter);
        m_RecyclerView.setNestedScrollingEnabled(false);

        List<Map.Entry<String, String>> dataset = new ArrayList<Map.Entry<String, String>>();
        HashMap<Integer, Map.Entry<String, String>> cryptocurrencies = CryptocurrencyInfo.getInstance().getCryptocurrencyNames();
        Collection<Map.Entry<String, String>> entries = cryptocurrencies.values();
        for(Map.Entry<String, String> entry : entries) {
            dataset.add(entry);
        }
        m_Adapter.setDataset(dataset);

        SearchView searchView = (SearchView)view.findViewById(R.id.search_view);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchString) {
                if(searchString == null)
                    return false;
                List<Map.Entry<String, String>> dataset = new ArrayList<Map.Entry<String, String>>();
                HashMap<Integer, Map.Entry<String, String>> cryptocurrencies = CryptocurrencyInfo.getInstance().getCryptocurrencyNames();
                Collection<Map.Entry<String, String>> entries = cryptocurrencies.values();
                HashMap<String, Set<String>> tradingPairs = CryptocurrencyInfo.getInstance().getTradingPairs();
                for(Map.Entry<String, String> entry : entries) {
                    String name = entry.getKey();
                    String symbol = entry.getValue();
                    if(name.toLowerCase().contains(searchString.toLowerCase())
                        || symbol.toLowerCase().contains(searchString.toLowerCase())) {
                        //check if symbol is contained in the trading pair (if searching that way) {
                        dataset.add(new HashMap.SimpleEntry<String, String>(name, symbol));
                        //change this
                        //if(name.equalsIgnoreCase(searchString)
                                //|| symbol.equalsIgnoreCase(searchString)) {
                            //display all available trading pairs
                            Set<String> symbols = CryptocurrencyInfo.getInstance().getTradingPairs(symbol);
                            if(symbols != null) {
                                for (String pair : symbols) {
                                    if(pair.toLowerCase().contains(searchString))
                                        if(pair.equals(symbol))
                                            continue;
                                        dataset.add(new HashMap.SimpleEntry<String, String>(name, pair));
                                }
                            }
                        //}
                    }
                    else if(searchString.toLowerCase().contains(symbol.toLowerCase())) {
                        Set<String> symbols = CryptocurrencyInfo.getInstance().getTradingPairs(symbol);
                        if(symbols != null) {
                            for (String pair : symbols) {
                                if(pair.toLowerCase().contains(searchString))
                                    dataset.add(new HashMap.SimpleEntry<String, String>(name, pair));
                            }
                        }
                    }
                }
                m_Adapter.setDataset(dataset);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                m_FragmentListener.onPopFragmentBackstack(null, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
