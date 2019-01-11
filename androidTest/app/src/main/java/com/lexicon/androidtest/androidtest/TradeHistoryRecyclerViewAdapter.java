package com.lexicon.androidtest.androidtest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class TradeHistoryRecyclerViewAdapter extends RecyclerView.Adapter<TradeHistoryRecyclerViewAdapter.ViewHolder> {

    List<TradeHistoryEntity> m_Entities;

    private boolean m_LongPressed = false;
    private OnLongClickListener m_OnLongClickListener;
    private OnCheckBoxClickedListener m_OnCheckBoxClickedListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout m_LinearLayout;
        public TextView m_TransactionTextView;
        public TextView m_SymbolTextView;
        public TextView m_QuantityTextView;
        public TextView m_PriceTextView;
        public TextView m_ValueTextView;
        public TextView m_DateTextView;

        public TextView m_SellPriceTextView;
        public TextView m_BuyPriceTextView;
        public TextView m_ValueFromCurrencyTextView;
        public TextView m_ValueToCurrencyTextView;
        public TextView m_TotalCostTextView;
        public TextView m_TotalCostFromTextView;
        public TextView m_TotalCostToTextView;
        public TextView m_CurrentValueTextView;
        public TextView m_ProfitTextView;

        public int m_Id;

        public ViewHolder(@NonNull LinearLayout layout, int type) {
            super(layout);
            m_LinearLayout = layout;
            m_TransactionTextView = layout.findViewById(R.id.transaction_text_view);
            m_SymbolTextView = layout.findViewById(R.id.symbol_text_view);
            m_QuantityTextView = layout.findViewById(R.id.quantity_text_view);
            /*m_PriceTextView = layout.findViewById(R.id.sell_price_text_view);
            m_ValueTextView = layout.findViewById(R.id.value_text_view);*/
            m_DateTextView = layout.findViewById(R.id.date_text_view);
        }
    }

    public interface OnLongClickListener {
        void onLongClicked(View view);
    }

    public interface OnCheckBoxClickedListener {
        void onClicked(TradeHistoryRecyclerViewAdapter.ViewHolder viewHolder);
    }

    public TradeHistoryRecyclerViewAdapter(List<TradeHistoryEntity> entities) {
        m_Entities = entities;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        m_OnLongClickListener = listener;
    }

    public void setOnCheckBoxClickedListener(OnCheckBoxClickedListener listener) {
        m_OnCheckBoxClickedListener = listener;
    }

    public void cancelLongClick() {
        m_LongPressed = false;
    }

    public void setEntities(List<TradeHistoryEntity> entities) {
        m_Entities = entities;
    }

    public List<TradeHistoryEntity> getEntites() {
        return m_Entities;
    }

    public void removeTradeHistoryEntities(List<Integer> entityIds) {
        Iterator<TradeHistoryEntity> iterator = m_Entities.iterator();
        while(iterator.hasNext()) {
            TradeHistoryEntity entity = iterator.next();
            if(entityIds.contains(entity.getId())) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        TradeHistoryEntity entity = m_Entities.get(position);
        if(entity.getTransactionType()) {
            return 1;
        }
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LinearLayout linearLayout;
        if(viewType == 0)  {
            linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_history_recycler_view_item, parent, false);
        }
        else {
            linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_history_recycler_view_item_2, parent, false);
        }
        final TradeHistoryRecyclerViewAdapter.ViewHolder vh = new TradeHistoryRecyclerViewAdapter.ViewHolder(linearLayout, viewType);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_LongPressed) {
                    CheckBox checkBox = vh.itemView.findViewById(R.id.check_box);
                    checkBox.setChecked(!checkBox.isChecked());
                    if(m_OnCheckBoxClickedListener != null)
                        m_OnCheckBoxClickedListener.onClicked(vh);
                    return;
                }
            }
        });
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!m_LongPressed) {
                    if(m_OnLongClickListener != null)
                        m_OnLongClickListener.onLongClicked(view);
                    m_LongPressed = true;
                }
                return true;
            }
        });
        CheckBox checkBox = vh.itemView.findViewById(R.id.check_box);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(m_OnCheckBoxClickedListener != null)
                    m_OnCheckBoxClickedListener.onClicked(vh);
            }
        });
        if(viewType == 0) {
            vh.m_BuyPriceTextView = linearLayout.findViewById(R.id.buy_price_text_view);
            vh.m_SellPriceTextView = linearLayout.findViewById(R.id.sell_price_text_view);
            //vh.m_ValueToCurrencyTextView = linearLayout.findViewById(R.id.value_to_currency_text_view);
            vh.m_ValueFromCurrencyTextView = linearLayout.findViewById(R.id.value_from_currency_text_view);
            vh.m_TotalCostTextView = linearLayout.findViewById(R.id.total_cost_text_view);
            vh.m_ProfitTextView = linearLayout.findViewById(R.id.profit_text_view);
        }
        else {
            vh.m_BuyPriceTextView = linearLayout.findViewById(R.id.buy_price_text_view);
            vh.m_TotalCostFromTextView = linearLayout.findViewById(R.id.cost_from_text_view);
            //vh.m_TotalCostToTextView = linearLayout.findViewById(R.id.cost_to_text_view);
            //vh.m_CurrentValueTextView = linearLayout.findViewById(R.id.current_value_text_view);
        }
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TradeHistoryEntity entity = m_Entities.get(position);
        holder.m_Id = entity.getId();
        holder.m_TransactionTextView.setText(entity.getTransactionType() ? "BUY" : "SELL");
        holder.m_TransactionTextView.setTextColor(Color.BLACK);
        holder.m_SymbolTextView.setText(entity.getSymbol());
        String[] pair = entity.getSymbol().split("-");
        holder.m_QuantityTextView.setText(entity.getQuantity() + " " + pair[0]);
        //holder.m_PriceTextView.setText(entity.getPurchasePrice());
        double quantity = Double.parseDouble(entity.getQuantity());
        double purchasePrice = -1;
        if(!entity.getPurchasePrice().equals("N/A")) {
            purchasePrice = Double.parseDouble(entity.getPurchasePrice());
        }
        double sellPrice = Double.parseDouble(entity.getSellPrice());
        //holder.m_ValueTextView.setText(new DecimalFormat("#.##").format(quantity * purchasePrice));
        Date date = new Date(entity.getDate());
        holder.m_DateTextView.setText(new SimpleDateFormat("MM/dd/yy", Locale.US).format(date));

        //String[] symbols = { entity.getSymbol(), pair[0] + "-USD" };
        //List<Cryptocurrency> cryptocurrencyList = CryptocurrencyInfo.getInstance().getPrice(symbols);
        //Cryptocurrency cryptocurrency = CryptocurrencyInfo.getInstance().getPrice(entity.getSymbol());
        //String price = cryptocurrency.getPrice();
        Double cost = quantity * purchasePrice;
        Double proceeds = quantity * sellPrice;
        Double profit = proceeds - cost;
        Double percentGained = ((sellPrice - purchasePrice) / purchasePrice) * 100d;
        //Double doublePrice = Double.parseDouble(price);
        //Double proceedsTo = proceeds / doublePrice;
        DecimalFormat df = new DecimalFormat("#.##");

        //Double usdPrice = Double.parseDouble(toUSD.getPrice());

        if(!entity.getTransactionType()) {
            //holder.m_ValueToCurrencyTextView.setText(new DecimalFormat("#.##").format(proceedsTo));
            holder.m_ValueFromCurrencyTextView.setText(DisplayFormat.formatString(String.valueOf(proceeds)) + " " + pair[1]);
            if(purchasePrice == -1) {
                holder.m_BuyPriceTextView.setText("N/A");
                holder.m_SellPriceTextView.setText(DisplayFormat.formatString(String.valueOf(sellPrice)) + " " + pair[1]);
                holder.m_TotalCostTextView.setText("N/A");
                holder.m_ProfitTextView.setText("N/A");
            }
            else {
                holder.m_BuyPriceTextView.setText(DisplayFormat.formatString(String.valueOf(purchasePrice)) + " " + pair[1]);
                holder.m_SellPriceTextView.setText(DisplayFormat.formatString(String.valueOf(sellPrice)) + " " + pair[1]);
                holder.m_TotalCostTextView.setText(DisplayFormat.formatString(String.valueOf(quantity * purchasePrice)) + " " + pair[1]);
                String profitString = DisplayFormat.formatString(String.valueOf(profit)) + " " + pair[1] + "\n" + "("
                        + (percentGained >= 0 ? "+" : "") + df.format(percentGained) + "%)";
                holder.m_ProfitTextView.setText(profitString);
            }
        }
        else {
            holder.m_BuyPriceTextView.setText(DisplayFormat.formatString(String.valueOf(purchasePrice)) + " " + pair[1]);
            holder.m_TotalCostFromTextView.setText(DisplayFormat.formatString(String.valueOf(quantity * purchasePrice)) + " " + pair[1]);
        }
    }

    @Override
    public int getItemCount() {
        return m_Entities.size();
    }
}
