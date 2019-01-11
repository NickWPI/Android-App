package com.lexicon.androidtest.androidtest;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {
    private List<Map.Entry<String, String>> m_Dataset;

    private OnLaunchFragmentListener m_OnLaunchFragmentListener;
    private OnSearchDetailsListener m_OnSearchDetailsListener;
    private Fragment m_Fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView m_NameTextView;
        public TextView m_SymbolTextView;

        public ViewHolder(@NonNull LinearLayout layout) {
            super(layout);
            m_NameTextView = (TextView)layout.getChildAt(0);
            m_SymbolTextView = (TextView)layout.getChildAt(1);
        }
    }

    public void setDataset(List<Map.Entry<String, String>> dataset) {
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

    public void setOnSearchDetailsListener(OnSearchDetailsListener listener) {
        m_OnSearchDetailsListener = listener;
    }

    public void setFragment(Fragment fragment) {
        m_Fragment = fragment;
    }

    @NonNull
    @Override
    public SearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_recycler_view_item, parent, false);
        final SearchRecyclerViewAdapter.ViewHolder vh = new SearchRecyclerViewAdapter.ViewHolder(linearLayout);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*SearchDetailsActivity activity = (SearchDetailsActivity)parent.getContext();
                Intent intent = new Intent(activity, DisplayDetailsFragment.class);
                String name = vh.m_NameTextView.getText().toString();
                String symbol = vh.m_SymbolTextView.getText().toString();
                intent.putExtra("Name", name);
                intent.putExtra("Symbol", symbol);
                //this needs to be optimized, pretty inefficient
                String[] symbols = { symbol };
                List<Cryptocurrency> crypto = CryptocurrencyInfo.getInstance().getPrice(symbols);
                if(crypto.size() == 0) {
                    Toast.makeText(activity, "Cannot retrieve price of that currency" , Toast.LENGTH_SHORT).show();
                    return;
                }
                Cryptocurrency cryptocurrency = crypto.get(0);
                intent.putExtra("Price", cryptocurrency.getPrice());
                intent.putExtra("PercentChange", cryptocurrency.getPercentageGained24h());
                intent.putExtra("PriceChange", cryptocurrency.getPriceChange());
                activity.startActivity(intent);
                activity.finish();*/
                m_OnLaunchFragmentListener.onPopFragmentBackstack(null, 0);
                String name = vh.m_NameTextView.getText().toString();
                String symbol = vh.m_SymbolTextView.getText().toString();
                m_OnSearchDetailsListener.onSearchResult(m_Fragment, name, symbol);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerViewAdapter.ViewHolder holder, int position) {
        Map.Entry<String, String> entry = m_Dataset.get(position);
        String name = entry.getKey();
        String symbol = entry.getValue();
        holder.m_NameTextView.setText(name);
        holder.m_SymbolTextView.setText(symbol);
    }

    @Override
    public int getItemCount() {
        if(m_Dataset == null)
            return 0;
        return m_Dataset.size();
    }
}
