package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NewsFragment extends Fragment {

    private OnDrawerSelectedListener m_DrawerListener;
    private OnLaunchFragmentListener m_FragmentListener;

    private RecyclerView m_RecyclerView;
    private RecyclerView.Adapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            m_DrawerListener = (OnDrawerSelectedListener) context;
            m_FragmentListener = (OnLaunchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDrawerSelectedListener and OnLaunchFragmentListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, parent, false);
        setHasOptionsMenu(true);

        Toolbar myToolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(myToolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        m_RecyclerView = view.findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this.getContext());
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        m_Adapter = new NewsRecyclerViewAdapter();
        m_RecyclerView.setAdapter(m_Adapter);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                m_DrawerListener.onDrawerSelected(item.getItemId());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
