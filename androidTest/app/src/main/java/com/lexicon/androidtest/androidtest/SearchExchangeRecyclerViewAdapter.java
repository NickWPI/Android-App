package com.lexicon.androidtest.androidtest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class SearchExchangeRecyclerViewAdapter extends RecyclerView.Adapter<SearchExchangeRecyclerViewAdapter.ViewHolder> {
    private List<String> m_Dataset;

    private OnLaunchFragmentListener m_OnLaunchFragmentListener;
    private OnSearchExchangeListener m_OnSearchExchangeListener;
    private Fragment m_Fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView m_ExchangeTextView;

        public ViewHolder(@NonNull ConstraintLayout layout) {
            super(layout);
            m_ExchangeTextView = (TextView)layout.getChildAt(0);
        }
    }

    public void setDataset(List<String> dataset) {
        if(m_Dataset == null) {
            m_Dataset = dataset;
            return;
        }
        m_Dataset = dataset;
        notifyDataSetChanged();
    }

    public void setOnLaunchFragmentListener(OnLaunchFragmentListener listener) {
        m_OnLaunchFragmentListener = listener;
    }

    public void setOnSearchExchangeListener(OnSearchExchangeListener listener) {
        m_OnSearchExchangeListener = listener;
    }

    public void setFragment(Fragment fragment) {
        m_Fragment = fragment;
    }

    @NonNull
    @Override
    public SearchExchangeRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_exchange_recycler_view_item, parent, false);
        final SearchExchangeRecyclerViewAdapter.ViewHolder vh = new SearchExchangeRecyclerViewAdapter.ViewHolder(layout);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_OnLaunchFragmentListener.onPopFragmentBackstack(null, 0);
                String exchange = vh.m_ExchangeTextView.getText().toString();
                m_OnSearchExchangeListener.onSearchExchangeResult(exchange);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchExchangeRecyclerViewAdapter.ViewHolder holder, int position) {
        String exchange = m_Dataset.get(position);
        holder.m_ExchangeTextView.setText(exchange);
    }

    @Override
    public int getItemCount() {
        if(m_Dataset == null)
            return 0;
        return m_Dataset.size();
    }
}
