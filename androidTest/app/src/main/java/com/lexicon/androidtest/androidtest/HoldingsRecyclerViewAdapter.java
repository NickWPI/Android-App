package com.lexicon.androidtest.androidtest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HoldingsRecyclerViewAdapter extends RecyclerView.Adapter<HoldingsRecyclerViewAdapter.ViewHolder> {

    List<PortfolioEntity> m_PortfolioEntities;

    OnLaunchFragmentListener m_OnLaunchFragmentListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements CryptocurrencyUpdateReceiver {

        public LinearLayout m_LinearLayout;
        public TextView m_NameTextView;
        public TextView m_SymbolTextView;
        public TextView m_QuantityTextView;
        public TextView m_PurchasePriceTextView;
        public TextView m_TotalPercentageChangeTextView;
        public TextView m_TotalPriceChangeTextView;
        public TextView m_PercentageChangeTextView;
        public TextView m_PriceChangeTextView;

        public ViewHolder(@NonNull LinearLayout layout) {
            super(layout);
            m_LinearLayout = layout;
            LinearLayout layout1 = (LinearLayout)m_LinearLayout.getChildAt(0);
            m_NameTextView = (TextView)layout1.getChildAt(0);
            m_SymbolTextView = (TextView)layout1.getChildAt(1);
            m_QuantityTextView = (TextView)m_LinearLayout.getChildAt(1);
            m_PurchasePriceTextView = (TextView)m_LinearLayout.getChildAt(2);
            LinearLayout layout2 = (LinearLayout)m_LinearLayout.getChildAt(3);
            m_PercentageChangeTextView = (TextView)layout2.getChildAt(0);
            m_PriceChangeTextView = (TextView)layout2.getChildAt(1);
            LinearLayout layout3 = (LinearLayout)m_LinearLayout.getChildAt(4);
            m_TotalPercentageChangeTextView = (TextView)layout3.getChildAt(0);
            m_TotalPriceChangeTextView = (TextView)layout3.getChildAt(1);
        }

        public void setQuantity(String quantity) {
            m_QuantityTextView.setText(quantity);
        }

        public void setPurchasePrice(String purchasePrice) {
            m_PurchasePriceTextView.setText(purchasePrice);
        }

        @Override
        public void setName(String name) {
            m_NameTextView.setText(name);
        }

        @Override
        public void setSymbol(String symbol) {
            m_SymbolTextView.setText(symbol);
        }

        @Override
        public void setPrice(String price) {
            Double currentPrice = Double.parseDouble(price);
            Double purchasePrice = Double.parseDouble(m_PurchasePriceTextView.getText().toString());
            Double totalChange = currentPrice - purchasePrice;
            Double totalPercentageChange = (totalChange / purchasePrice) * 100;
            DecimalFormat df = new DecimalFormat();
            String sChange = "";
            String totalChangeString = "";
            //for percentage
            if(Math.abs(totalPercentageChange) < 0.01) {
                df.applyPattern("#.####");
                df.setMinimumFractionDigits(4);
                sChange = df.format(totalPercentageChange);
            } else {
                df.applyPattern("#.##");
                df.setMinimumFractionDigits(2);
                sChange = df.format(totalPercentageChange);
            }
            //for change
            if(Math.abs(totalChange) < 0.01) {
                df.applyPattern("#.####");
                df.setMinimumFractionDigits(4);
                totalChangeString = df.format(totalChange);
            } else {
                df.applyPattern("#.##");
                df.setMinimumFractionDigits(2);
                totalChangeString = df.format(totalChange);
            }
            if(totalPercentageChange < 0) {
                m_TotalPercentageChangeTextView.setTextColor(Color.RED);
            }
            else if(totalPercentageChange == 0) {
                m_TotalPercentageChangeTextView.setTextColor(Color.GRAY);
            }
            else {
                m_TotalPercentageChangeTextView.setTextColor(Color.rgb(100,221,23));
                totalChangeString = "+" + totalChangeString;
                sChange = "+" + sChange;
            }
            m_TotalPercentageChangeTextView.setText(sChange + "%");
            m_TotalPriceChangeTextView.setText(totalChangeString);
        }

        @Override
        public void setPriceChange(String change) {
            Double priceChange = Double.parseDouble(change);
            if(priceChange < 0) {
                m_PercentageChangeTextView.setTextColor(Color.RED);
            }
            else if(priceChange == 0) {
                m_PercentageChangeTextView.setTextColor(Color.GRAY);
            }
            else {
                m_PercentageChangeTextView.setTextColor(Color.rgb(100,221,23));
                change = "+" + change;
            }
            m_PriceChangeTextView.setText(change);
        }

        @Override
        public void setPercentageGained(String percentage) {
            m_PercentageChangeTextView.setText(percentage);
        }

        @Override
        public void setImageBitmap(Bitmap bitmap) {

        }
    }

    public HoldingsRecyclerViewAdapter(List<PortfolioEntity> entities, OnLaunchFragmentListener listener) {
        Collections.sort(entities, new Comparator<PortfolioEntity>() {
            public int compare(PortfolioEntity e1, PortfolioEntity e2) {
                if (e1.getOrder() < e2.getOrder())
                    return -1;
                if (e1.getOrder() > e2.getOrder())
                    return 1;
                return 0;
            }
        });
        m_PortfolioEntities = entities;
        m_OnLaunchFragmentListener = listener;
    }

    public void setPortfolioEntities(List<PortfolioEntity> entities) {
        Collections.sort(entities, new Comparator<PortfolioEntity>() {
            public int compare(PortfolioEntity e1, PortfolioEntity e2) {
                if (e1.getOrder() < e2.getOrder())
                    return -1;
                if (e1.getOrder() > e2.getOrder())
                    return 1;
                return 0;
            }
        });
        m_PortfolioEntities = entities;
    }

    @NonNull
    @Override
    public HoldingsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holdings_recycler_view_item, parent, false);
        final HoldingsRecyclerViewAdapter.ViewHolder vh = new HoldingsRecyclerViewAdapter.ViewHolder(linearLayout);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Name", vh.m_NameTextView.getText().toString());
                bundle.putString("Symbol", vh.m_SymbolTextView.getText().toString());
                bundle.putString("Quantity", vh.m_QuantityTextView.getText().toString());
                bundle.putString("Average Cost", vh.m_PurchasePriceTextView.getText().toString());
                //price, daily change, total change
                m_OnLaunchFragmentListener.onLaunchFragment(TradeHistoryDetailsFragment.class, bundle, true);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HoldingsRecyclerViewAdapter.ViewHolder holder, int position) {
        PortfolioEntity entity = m_PortfolioEntities.get(position);
        holder.setName(entity.getName());
        String modifiedSymbol = entity.getSymbol().split("-").length == 1 ?
                entity.getSymbol() + "-USD" : entity.getSymbol();
        holder.setSymbol(modifiedSymbol);
        holder.setQuantity(entity.getQuantity());
        holder.setPurchasePrice(entity.getPurchasePrice());
        CryptocurrencyUIUpdateManager.getInstance().addViewHolder(modifiedSymbol, holder);
    }

    @Override
    public void onViewRecycled(@NonNull HoldingsRecyclerViewAdapter.ViewHolder viewHolder) {
        String symbol = viewHolder.m_SymbolTextView.getText().toString();
        CryptocurrencyUIUpdateManager.getInstance().removeViewHolder(symbol);
    }

    @Override
    public int getItemCount() {
        return m_PortfolioEntities.size();
    }
}
