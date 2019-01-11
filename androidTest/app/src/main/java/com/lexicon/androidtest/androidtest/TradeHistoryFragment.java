package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TradeHistoryFragment extends Fragment implements PortfolioFragment.FragmentLifecycle {

    private OnLaunchFragmentListener m_FragmentListener;

    private RecyclerView m_RecyclerView;
    private TradeHistoryRecyclerViewAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    List<TradeHistoryEntity> m_Entities;

    List<Integer> m_DeletionList;

    private boolean m_UseItemRemovalMenu = false;

    public TradeHistoryFragment() {
        m_DeletionList = new ArrayList<Integer>();
    }

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
        View view = inflater.inflate(R.layout.fragment_trade_history, parent, false);
        setHasOptionsMenu(true);

        m_RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this.getContext());
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        TradeHistoryDao tradeHistoryDao = ApplicationDatabase.getInstance(this.getContext()).tradeHistoryDao();
        m_Entities = tradeHistoryDao.getAll();

        m_Adapter = new TradeHistoryRecyclerViewAdapter(m_Entities);
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(10));
        m_RecyclerView.setAdapter(m_Adapter);

        m_Adapter.setOnLongClickListener(new TradeHistoryRecyclerViewAdapter.OnLongClickListener() {
            @Override
            public void onLongClicked(View view) {
                int count = m_Adapter.getItemCount();
                for(int i = 0; i < count; i++) {
                    TradeHistoryRecyclerViewAdapter.ViewHolder viewHolder
                            = (TradeHistoryRecyclerViewAdapter.ViewHolder)m_RecyclerView.findViewHolderForAdapterPosition(i);
                    CheckBox checkBox = viewHolder.itemView.findViewById(R.id.check_box);
                    checkBox.setVisibility(View.VISIBLE);
                }

                //setup item removal menu
                PortfolioFragment portfolioFragment = (PortfolioFragment)FragmentRepository.getInstance().getFragment(PortfolioFragment.class.getName());
                portfolioFragment.setEnableNavigationDrawer(false);
                AppCompatActivity activity = (AppCompatActivity)TradeHistoryFragment.this.getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("Remove Items");
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                m_UseItemRemovalMenu = true;
                activity.invalidateOptionsMenu();
            }
        });

        m_Adapter.setOnCheckBoxClickedListener(new TradeHistoryRecyclerViewAdapter.OnCheckBoxClickedListener() {
            @Override
            public void onClicked(TradeHistoryRecyclerViewAdapter.ViewHolder viewHolder) {
                int id = viewHolder.m_Id;
                CheckBox checkBox = viewHolder.itemView.findViewById(R.id.check_box);
                boolean checked = checkBox.isChecked();
                if(checked) {
                    m_DeletionList.add(id);
                }
                else {
                    m_DeletionList.remove(Integer.valueOf(id));
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if(m_UseItemRemovalMenu) {
            inflater.inflate(R.menu.item_remove_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    private void cancelItemRemovalMenu() {
        m_DeletionList.clear();
        int count = m_Adapter.getItemCount();
        for(int i = 0; i < count; i++) {
            TradeHistoryRecyclerViewAdapter.ViewHolder viewHolder
                    = (TradeHistoryRecyclerViewAdapter.ViewHolder)m_RecyclerView.findViewHolderForAdapterPosition(i);
            if(viewHolder == null)
                continue;
            CheckBox checkBox = viewHolder.itemView.findViewById(R.id.check_box);
            if(checkBox.isChecked())
                checkBox.setChecked(false);
            checkBox.setVisibility(View.GONE);
        }

        PortfolioFragment portfolioFragment = (PortfolioFragment)FragmentRepository.getInstance().getFragment(PortfolioFragment.class.getName());
        portfolioFragment.setEnableNavigationDrawer(true);
        m_Adapter.cancelLongClick();
        AppCompatActivity activity = (AppCompatActivity)TradeHistoryFragment.this.getActivity();
        m_UseItemRemovalMenu = false;
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Portfolio");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        activity.invalidateOptionsMenu();
    }

    private void uncheckAllBoxes() {
        int count = m_Adapter.getItemCount();
        for(int i = 0; i < count; i++) {
            RecyclerView.ViewHolder viewHolder = m_RecyclerView.findViewHolderForAdapterPosition(i);
            if(viewHolder == null) {
                continue;
            }
            CheckBox checkBox = viewHolder.itemView.findViewById(R.id.check_box);
            if(checkBox.isChecked())
                checkBox.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("TRADEHISTORY", "options selected");
        switch (item.getItemId()) {
            case android.R.id.home:
                if(m_UseItemRemovalMenu)
                    cancelItemRemovalMenu();
                break;
            case R.id.cancel:
                cancelItemRemovalMenu();
                break;
            case R.id.delete:
                uncheckAllBoxes();
                TradeHistoryDao tradeHistoryDao = ApplicationDatabase.getInstance(this.getContext()).tradeHistoryDao();
                for(TradeHistoryEntity entity : m_Entities) {
                    if(m_DeletionList.contains(entity.getId())) {
                        tradeHistoryDao.delete(entity);
                    }
                }
                m_Adapter.removeTradeHistoryEntities(m_DeletionList);
                Toast.makeText(this.getContext(), "Removed " + m_DeletionList.size() + " items" , Toast.LENGTH_SHORT).show();
                m_DeletionList.clear();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPauseFragment() {
        cancelItemRemovalMenu();
    }

    @Override
    public void onResumeFragment() {

    }
}
