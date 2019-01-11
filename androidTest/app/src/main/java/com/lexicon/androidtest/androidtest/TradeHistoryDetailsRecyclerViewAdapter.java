package com.lexicon.androidtest.androidtest;

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

/*public class TradeHistoryDetailsRecyclerViewAdapter extends RecyclerView.Adapter<TradeHistoryDetailsRecyclerViewAdapter.ViewHolder> {

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

        public int m_Id;

        public ViewHolder(@NonNull LinearLayout layout) {
            super(layout);
            m_LinearLayout = layout;
            m_TransactionTextView = layout.findViewById(R.id.transaction_text_view);
            m_SymbolTextView = layout.findViewById(R.id.symbol_text_view);
            m_QuantityTextView = layout.findViewById(R.id.quantity_text_view);
            m_PriceTextView = layout.findViewById(R.id.price_text_view);
            m_ValueTextView = layout.findViewById(R.id.value_text_view);
            m_DateTextView = layout.findViewById(R.id.date_text_view);
        }
    }

    public interface OnLongClickListener {
        void onLongClicked(View view);
    }

    public interface OnCheckBoxClickedListener {
        void onClicked(TradeHistoryRecyclerViewAdapter.ViewHolder viewHolder);
    }

    public TradeHistoryDetailsRecyclerViewAdapter(List<TradeHistoryEntity> entities) {
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trade_history_recycler_view_item, parent, false);
        final TradeHistoryDetailsRecyclerViewAdapter.ViewHolder vh = new TradeHistoryDetailsRecyclerViewAdapter.ViewHolder(linearLayout);
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
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TradeHistoryEntity entity = m_Entities.get(position);
        holder.m_Id = entity.getId();
        holder.m_TransactionTextView.setText(entity.getTransactionType() ? "BUY" : "SELL");
        holder.m_TransactionTextView.setTextColor(Color.BLACK);
        holder.m_SymbolTextView.setText(entity.getSymbol());
        holder.m_QuantityTextView.setText(entity.getQuantity());
        holder.m_PriceTextView.setText(entity.getPurchasePrice());
        double quantity = Double.parseDouble(entity.getQuantity());
        double purchasePrice = Double.parseDouble(entity.getPurchasePrice());
        holder.m_ValueTextView.setText(new DecimalFormat("#.##").format(quantity * purchasePrice));
        Date date = new Date(entity.getDate());
        holder.m_DateTextView.setText(new SimpleDateFormat("MM/dd/yy", Locale.US).format(date));
    }

    @Override
    public int getItemCount() {
        return m_Entities.size();
    }
}
*/