package com.lexicon.androidtest.androidtest;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DisplayDetailsRecyclerViewAdapter extends RecyclerView.Adapter<DisplayDetailsRecyclerViewAdapter.ViewHolder> {

    private final List<String> m_Labels;
    private final LinkedHashMap<String, String> m_CryptocurrencyStats;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout m_LinearLayout;
        TextView m_LabelsTextView;
        TextView m_StatsTextView;

        public ViewHolder(@NonNull LinearLayout linearLayout) {
            super(linearLayout);
            m_LinearLayout = linearLayout;
            m_LabelsTextView = (TextView)m_LinearLayout.getChildAt(0);
            m_StatsTextView = (TextView)m_LinearLayout.getChildAt(1);
        }

        public void setLabelsText(String text) {
            m_LabelsTextView.setText(text);
        }

        public String getLabelsText() {
            return m_LabelsTextView.getText().toString();
        }

        public void setStatsText(String text) {
            m_StatsTextView.setText(text);
        }

        public String getStatsText() {
            return m_StatsTextView.getText().toString();
        }
    }

    public DisplayDetailsRecyclerViewAdapter(String symbol) {
        m_Labels = new ArrayList<String>();
        m_CryptocurrencyStats = new LinkedHashMap<String, String>();

        m_Labels.add("Start Date");
        m_Labels.add("Open");
        m_Labels.add("High");
        m_Labels.add("Low");
        m_Labels.add("Volume");
        m_Labels.add("Volume To");
        m_Labels.add("Algorithm");
        m_Labels.add("Proof Type");
        m_Labels.add("Total Supply");
        m_Labels.add("Current Supply");

        HashMap<String, String> stats = CryptocurrencyInfo.getInstance().getStatistics(symbol);
        m_CryptocurrencyStats.put("Start Date", stats.get("StartDate"));
        m_CryptocurrencyStats.put("Open", "N/A");
        m_CryptocurrencyStats.put("High", "N/A");
        m_CryptocurrencyStats.put("Low", "N/A");
        m_CryptocurrencyStats.put("Volume", "N/A");
        m_CryptocurrencyStats.put("Volume To", "N/A");
        m_CryptocurrencyStats.put("Algorithm", stats.get("Algorithm"));
        m_CryptocurrencyStats.put("Proof Type", stats.get("ProofType"));
        m_CryptocurrencyStats.put("Total Supply", stats.get("TotalCoinSupply"));
        m_CryptocurrencyStats.put("CurrentSupply", stats.get("TotalCoinsMined"));
    }

    public void setCryptocurrencyStatistic(String name, String content) {
        this.setCryptocurrencyStatistic(name, content, true);
    }

    public void setCryptocurrencyStatistic(String name, String content, boolean updateVH) {
        m_CryptocurrencyStats.put(name, content);
        if(updateVH) {
            int position = -1;
            int count = 0;
            for(String label : m_Labels) {
                if(label.equals(name)) {
                    position = count;
                    break;
                }
                count++;
            }
            if(position != -1) {
                notifyItemChanged(position);
            }
        }
    }

    @NonNull
    @Override
    public DisplayDetailsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_details_recycler_view_item, parent, false);
        ViewHolder vh = new ViewHolder(linearLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayDetailsRecyclerViewAdapter.ViewHolder holder, int position) {
        String label = m_Labels.get(position);
        holder.setLabelsText(label);
        holder.setStatsText(m_CryptocurrencyStats.get(label));
    }

    @Override
    public int getItemCount() {
        return m_CryptocurrencyStats.size();
    }
}
