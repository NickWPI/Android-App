package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;

public class CryptocurrencyListFragment extends Fragment implements OnSearchDetailsListener{

    private RecyclerView m_RecyclerView;
    private RecyclerViewAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    private OnDrawerSelectedListener m_DrawerListener;
    private OnLaunchFragmentListener m_FragmentListener;

    boolean m_Initialized = false;

    int m_AdapterSize;

    CryptocurrencyUpdater m_Updater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            m_DrawerListener = (OnDrawerSelectedListener) context;
            m_FragmentListener = (OnLaunchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDrawerSelectedListener and OnLaunchFragmentListener");
        }
        m_AdapterSize = 10;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_cryptocurrency_list, parent, false);
        setHasOptionsMenu(true);

        m_RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new PreCachingLayoutManager(this.getActivity(), 1200);
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        if(!m_Initialized) {
            //place in MainActivity, this code is trash
            CryptocurrencyInfo.getInstance().generateCryptocurrencyList();
            CryptocurrencyInfo.getInstance().generateCryptoDisplayList();
            CryptocurrencyInfo.getInstance().generateExchangeList();
            CryptocurrencyInfo.getInstance().generateNewsArticles();
            m_Updater = new CryptocurrencyUpdater();
            m_Updater.run();
            m_Initialized = true;
        }

        // specify an adapter (see also next example)
        m_Adapter = new RecyclerViewAdapter(m_AdapterSize);
        m_Adapter.setOnLaunchFragmentListener(m_FragmentListener);
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(5));
        m_RecyclerView.setAdapter(m_Adapter);
        FrameLayout progressBarHolder = (FrameLayout) view.findViewById(R.id.progress_bar_holder);
        m_Adapter.setProgressBarHolder(progressBarHolder);

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(myToolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        Log.d("FRAGMENT", "created cryptocurrencylistfragment");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
       m_AdapterSize = m_Adapter.getItemCount();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Bundle bundle = new Bundle();
                bundle.putString("FragmentName", this.getClass().getName());
                m_FragmentListener.onLaunchFragment(SearchDetailsFragment.class, bundle, true);
                return true;
            case android.R.id.home:
                m_DrawerListener.onDrawerSelected(item.getItemId());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchResult(Fragment fragment, String name, String symbol) {
        Bundle bundle = new Bundle();
        bundle.putString("Name", name);
        bundle.putString("Symbol", symbol);
        //this needs to be optimized, pretty inefficient
        Cryptocurrency cryptocurrency = CryptocurrencyInfo.getInstance().getPrice(symbol);
        if(cryptocurrency == null) {
            Toast.makeText(fragment.getActivity(), "Cannot retrieve price of that currency" , Toast.LENGTH_SHORT).show();
            return;
        }
        bundle.putString("Price", cryptocurrency.getPrice());
        bundle.putString("PercentChange", cryptocurrency.getPercentageGained24h());
        bundle.putString("PriceChange", cryptocurrency.getPriceChange());
        m_FragmentListener.onLaunchFragment(DisplayDetailsFragment.class, bundle, true);
    }
}
