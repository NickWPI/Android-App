package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchExchangeFragment extends Fragment {
    private RecyclerView m_RecyclerView;
    private SearchExchangeRecyclerViewAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    OnLaunchFragmentListener m_FragmentListener;
    OnSearchExchangeListener m_OnSearchExchangeListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            m_FragmentListener = (OnLaunchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLaunchFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_details, parent, false);
        setHasOptionsMenu(true);

        Bundle args = this.getArguments();
        String name = args.getString("FragmentName");
        String symbol = args.getString("Symbol");

        try {
            Fragment fragment = FragmentRepository.getInstance().getFragment(name);
            if(fragment == null) {
                FragmentManager manager = this.getActivity().getSupportFragmentManager();
                fragment = manager.findFragmentByTag(name);
            }
            m_OnSearchExchangeListener = (OnSearchExchangeListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(name + " must implement OnSearchExchangeListener");
        }

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) this.getActivity();
        activity.setSupportActionBar(myToolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        m_RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this.getContext());
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        m_Adapter = new SearchExchangeRecyclerViewAdapter();
        m_Adapter.setFragment(this);
        m_Adapter.setOnLaunchFragmentListener(m_FragmentListener);
        m_Adapter.setOnSearchExchangeListener(m_OnSearchExchangeListener);
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(5));
        m_RecyclerView.setAdapter(m_Adapter);
        m_RecyclerView.setNestedScrollingEnabled(false);

        final Set<String> exchanges = CryptocurrencyInfo.getInstance().getSupportedExchanges(symbol);
        List<String> dataset = new ArrayList<>(exchanges);
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
                List<String> dataset = new ArrayList<>();
                for(String exchange : exchanges) {
                    if(exchange.toLowerCase().contains(searchString.toLowerCase())) {
                        if(exchange.equals("CCCAGG")) {
                            dataset.add(0, exchange);
                        }
                        else {
                            dataset.add(exchange);
                        }
                    }
                }
                m_Adapter.setDataset(dataset);
                return false;
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
