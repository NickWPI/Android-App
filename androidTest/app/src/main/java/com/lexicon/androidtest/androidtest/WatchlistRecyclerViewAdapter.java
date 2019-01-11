package com.lexicon.androidtest.androidtest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WatchlistRecyclerViewAdapter extends RecyclerView.Adapter<WatchlistRecyclerViewAdapter.ViewHolder>
    implements RecyclerViewDragCallback.OnDragAndDropListener {

    private OnLaunchFragmentListener m_OnLaunchFragmentListener;

    private OnLongClickListener m_OnLongClickListener;
    private OnCheckBoxClickedListener m_OnCheckBoxClickedListener;

    private List<WatchlistEntity> m_WatchlistEntities;
    private Boolean m_LongPressed = false;

    private ItemTouchHelper m_TouchHelper;

    public static class ViewHolder extends RecyclerView.ViewHolder implements CryptocurrencyUpdateReceiver {
        public LinearLayout m_LinearLayout;
        public TextView m_NameTextView;
        public TextView m_SymbolTextView;
        public TextView m_PriceTextView;
        public TextView m_PercentageGainedTextView;
        public TextView m_PriceChangeTextView;
        public ImageView m_ImageView;

        public ViewHolder(LinearLayout layout) {
            super(layout);
            m_LinearLayout = layout;
            m_ImageView = (ImageView)m_LinearLayout.getChildAt(1);
            LinearLayout layout1 = (LinearLayout)m_LinearLayout.getChildAt(2);
            m_NameTextView = (TextView)layout1.getChildAt(0);
            m_SymbolTextView = (TextView)layout1.getChildAt(1);
            m_PriceTextView = (TextView)m_LinearLayout.getChildAt(3);
            LinearLayout layout2 = (LinearLayout)m_LinearLayout.getChildAt(4);
            m_PercentageGainedTextView = (TextView)layout2.getChildAt(0);
            m_PriceChangeTextView = (TextView)layout2.getChildAt(1);

        }

        @Override
        public void setName(String text) {
            m_NameTextView.setText(text);
        }

        @Override
        public void setSymbol(String text) {
            m_SymbolTextView.setText(text);
        }

        @Override
        public void setPrice(String text) {
            if(text.isEmpty()) {
                return;
            }
            text = DisplayFormat.formatString(text);
            m_PriceTextView.setText(text);
        }

        @Override
        public void setPriceChange(String change) {
            m_PriceChangeTextView.setText(change);
        }

        @Override
        public void setPercentageGained(String percentage) {
            m_PercentageGainedTextView.setText(percentage);
            Double percentChange = Double.parseDouble(percentage.replaceAll("%", "").replaceAll("\\+",""));
            if(percentChange < 0) {
                m_PercentageGainedTextView.setTextColor(Color.RED);
            }
            else if(percentChange == 0) {
                m_PercentageGainedTextView.setTextColor(Color.GRAY);
            }
            else {
                m_PercentageGainedTextView.setTextColor(Color.rgb(100,221,23));
            }
        }

        @Override
        public void setImageBitmap(Bitmap bitmap) {
            m_ImageView.setImageBitmap(bitmap);
        }
    }

    public interface OnLongClickListener {
        void onLongClick();
    }

    public interface OnCheckBoxClickedListener {
        void onClicked(ViewHolder viewHolder);
    }

    public WatchlistRecyclerViewAdapter(WatchlistRecyclerViewAdapter.OnLongClickListener listener) {
        m_OnLongClickListener = listener;
    }

    public WatchlistRecyclerViewAdapter(List<WatchlistEntity> entities, WatchlistRecyclerViewAdapter.OnLongClickListener listener) {
        Collections.sort(entities, new Comparator<WatchlistEntity>() {
            public int compare(WatchlistEntity e1, WatchlistEntity e2) {
                if (e1.getOrder() < e2.getOrder())
                    return -1;
                if (e1.getOrder() > e2.getOrder())
                    return 1;
                return 0;
            }
        });
        m_WatchlistEntities = entities;
        m_OnLongClickListener = listener;
    }

    public void setWatchlistEntities(List<WatchlistEntity> entities) {
        //sort the list by order
        Collections.sort(entities, new Comparator<WatchlistEntity>() {
            public int compare(WatchlistEntity e1, WatchlistEntity e2) {
                if (e1.getOrder() < e2.getOrder())
                    return -1;
                if (e1.getOrder() > e2.getOrder())
                    return 1;
                return 0;
            }
        });
        m_WatchlistEntities = entities;
    }

    public void addWatchlistEntity(WatchlistEntity entity) {
        m_WatchlistEntities.add(entity);
        //notifyDataSetChanged();
        notifyItemInserted(m_WatchlistEntities.size() - 1);
    }

    public void removeWatchlistEntity(String name) {

    }

    public boolean hasWatchlistEntity(String symbol) {
        for(WatchlistEntity entity : m_WatchlistEntities) {
            if(entity.getSymbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    public void removeWatchlistEntities(List<String> names) {
        Iterator<WatchlistEntity> iterator = m_WatchlistEntities.iterator();
        while(iterator.hasNext()) {
            WatchlistEntity entity = iterator.next();
            if(names.contains(entity.getName())) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    public void setOnLaunchFragmentListener(OnLaunchFragmentListener listener) {
        m_OnLaunchFragmentListener = listener;
    }

    public void setOnCheckBoxClickedListener(OnCheckBoxClickedListener listener) {
        m_OnCheckBoxClickedListener = listener;
    }

    public void startLongClick() {
        if(m_WatchlistEntities.size() > 0) {
            //remove the view param
            WatchlistRecyclerViewAdapter.this.m_OnLongClickListener.onLongClick();
            m_LongPressed = true;
        }
    }

    public void cancelLongClick() {
        m_LongPressed = false;
    }

    public boolean isLongClicked() {
        return m_LongPressed;
    }

    public void setItemTouchHelper(ItemTouchHelper touchHelper) {
        m_TouchHelper = touchHelper;
    }

    private void handleItemClick(ViewHolder vh) {
        if(m_LongPressed) {
            CheckBox checkBox = vh.itemView.findViewById(R.id.check_box);
            checkBox.setChecked(!checkBox.isChecked());
            WatchlistRecyclerViewAdapter.this.m_OnCheckBoxClickedListener.onClicked(vh);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("Name", vh.m_NameTextView.getText().toString());
        bundle.putString("Symbol", vh.m_SymbolTextView.getText().toString());
        bundle.putString("Price", vh.m_PriceTextView.getText().toString());
        bundle.putString("PercentChange", vh.m_PercentageGainedTextView.getText().toString());
        bundle.putString("PriceChange", vh.m_PriceChangeTextView.getText().toString());
        m_OnLaunchFragmentListener.onLaunchFragment(DisplayDetailsFragment.class, bundle, true);
    }

    private void handleItemLongClick(ViewHolder vh) {
        WatchlistRecyclerViewAdapter.this.m_OnLongClickListener.onLongClick();
        m_LongPressed = true;
    }

    //fuck disabled people
    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watchlist_recycler_view_item, parent, false);
        final WatchlistRecyclerViewAdapter.ViewHolder vh = new WatchlistRecyclerViewAdapter.ViewHolder(linearLayout);
        m_LongPressed = false;
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(m_LongPressed) {
                    CheckBox checkBox = vh.itemView.findViewById(R.id.check_box);
                    checkBox.setChecked(!checkBox.isChecked());
                    WatchlistRecyclerViewAdapter.this.m_OnCheckBoxClickedListener.onClicked(vh);
                    return;
                }
                Bundle bundle = new Bundle();
                String price = vh.m_PriceTextView.getText().toString();
                if(!price.isEmpty()) {
                    bundle.putString("Name", vh.m_NameTextView.getText().toString());
                    bundle.putString("Symbol", vh.m_SymbolTextView.getText().toString());
                    bundle.putString("Price", vh.m_PriceTextView.getText().toString());
                    bundle.putString("PercentChange", vh.m_PercentageGainedTextView.getText().toString());
                    bundle.putString("PriceChange", vh.m_PriceChangeTextView.getText().toString());
                    m_OnLaunchFragmentListener.onLaunchFragment(DisplayDetailsFragment.class, bundle, true);
                }
            }
        });
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(m_LongPressed) {
                    //m_TouchHelper.startDrag(vh);
                }
                else {
                    WatchlistRecyclerViewAdapter.this.m_OnLongClickListener.onLongClick();
                    m_LongPressed = true;
                }
                return true;
            }
        });

        ImageView imageView = vh.itemView.findViewById(R.id.image_view);
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (m_LongPressed) {
                        m_TouchHelper.startDrag(vh);
                    }
                }
                return false;
            }
        });
        CheckBox checkBox = vh.itemView.findViewById(R.id.check_box);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WatchlistRecyclerViewAdapter.this.m_OnCheckBoxClickedListener.onClicked(vh);
            }
        });
        return vh;
    }

    @Override
    public int getItemCount() {
        return m_WatchlistEntities.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //doesn't work
        if(m_LongPressed) {
            CheckBox checkBox = holder.itemView.findViewById(R.id.check_box);
            checkBox.setVisibility(View.VISIBLE);
        }
        WatchlistEntity entity = m_WatchlistEntities.get(position);
        final String name = entity.getName();
        String symbol = entity.getSymbol();
        holder.setName(name);
        holder.setSymbol(symbol);

        if(symbol.split("-").length == 1) {
            symbol = symbol + "-USD";
        }

        final String modifiedSymbol = symbol;

        //implement a get most recent price thing where u can grab
        //the previous price as a filler
        HashMap<String, Cryptocurrency> priceList = CryptocurrencyInfo.getInstance().getPreviousPriceList();
        Cryptocurrency cryptocurrency = priceList.get(symbol);
        if(cryptocurrency != null) {
            holder.setPrice(cryptocurrency.getPrice());
            holder.setPriceChange(cryptocurrency.getPriceChange());
            holder.setPercentageGained(cryptocurrency.getPercentageGained24h());
        }

        //use trade string symbols instead
        CryptocurrencyUIUpdateManager.getInstance().addViewHolder(symbol, holder);

        boolean loaded = CryptocurrencyInfo.getInstance().isImageLoaded(symbol);
        if(!loaded) {
            //load image view asynchronously
            CryptocurrencyInfo.getInstance().loadImageAsync(symbol.split("-")[0], new CryptocurrencyInfo.OnLoadImageListener() {
                @Override
                public void onLoad(String symbol, Bitmap bitmap) {
                    Map.Entry<String, Bitmap> data = new HashMap.SimpleEntry<String, Bitmap>(modifiedSymbol, bitmap);
                    Message message = new Message();
                    CryptocurrencyUIUpdateManager.DataObject msgObj = new CryptocurrencyUIUpdateManager.DataObject();
                    msgObj.m_Data = data;
                    msgObj.m_UpdateOptions = CryptocurrencyUIUpdateManager.UpdateOptions.UPDATE_IMAGE;
                    message.obj = msgObj;
                    CryptocurrencyUIUpdateManager.getInstance().getHandler().sendMessage(message);
                }
            });
        }
        else {
            Bitmap bitmap = CryptocurrencyInfo.getInstance().getImage(symbol.split("-")[0]);
            holder.m_ImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder viewHolder) {
        String symbol = viewHolder.m_SymbolTextView.getText().toString();
        //int position = viewHolder.getAdapterPosition();
        //WatchlistEntity entity = m_WatchlistEntities.get(position);
        //String name = entity.getName();
        CryptocurrencyUIUpdateManager.getInstance().removeViewHolder(symbol);
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        //also switch the order
        Collections.swap(m_WatchlistEntities, oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
        int count = 0;
        for(WatchlistEntity entity : m_WatchlistEntities) {
            entity.setOrder(count);
        }
    }

    @Override
    public void onViewSwiped(int position, int direction) {
        //no swiping to delete
    }
}
