package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.List;

public class HoldingsFragment extends Fragment implements PortfolioFragment.FragmentLifecycle {

    private RecyclerView m_RecyclerView;
    private RecyclerView.Adapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    List<PortfolioEntity> m_PortfolioEntities;

    boolean m_Update = false;

    private OnLaunchFragmentListener m_FragmentListener;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_holdings, parent, false);

        m_RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this.getContext());
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        PortfolioDao portfolioDao = ApplicationDatabase.getInstance(this.getContext()).portfolioDao();
        List<PortfolioEntity> portfolioEntities = portfolioDao.getAll();
        m_PortfolioEntities = portfolioEntities;

        m_Adapter = new HoldingsRecyclerViewAdapter(m_PortfolioEntities, m_FragmentListener);
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(5));
        m_RecyclerView.setAdapter(m_Adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPauseFragment() {
    }

    @Override
    public void onResumeFragment() {
        if(m_Update) {
            PortfolioDao portfolioDao = ApplicationDatabase.getInstance(this.getContext()).portfolioDao();
            ((HoldingsRecyclerViewAdapter) m_Adapter).setPortfolioEntities(portfolioDao.getAll());
            m_Adapter.notifyDataSetChanged();
            m_Update = false;
        }
    }

    public void setUpdate(boolean update) {
        m_Update = update;
    }
}
