package com.lexicon.androidtest.androidtest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    //private String[] mDataset;
    private OnLaunchFragmentListener m_OnLaunchFragmentListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements CryptocurrencyUpdateReceiver {
        // each data item is just a string in this case
        public LinearLayout m_LinearLayout;
        public TextView m_NameTextView;
        public TextView m_SymbolTextView;
        public TextView m_PriceTextView;
        public TextView m_PercentageGained1hTextView;
        public TextView m_PercentageGained24hTextView;
        public TextView m_PriceChangeTextView;
        public TextView m_PercentageGained7dTextView;
        public ImageView m_ImageView;
        public TextView m_NumberTextView;

        public ConstraintLayout m_Footer;

        public ViewHolder(LinearLayout layout) {
            super(layout);
            m_LinearLayout = layout;
            m_NumberTextView = (TextView)m_LinearLayout.getChildAt(0);
            m_ImageView = (ImageView)m_LinearLayout.getChildAt(1);
            LinearLayout layout1 = (LinearLayout)m_LinearLayout.getChildAt(2);
            m_NameTextView = (TextView)layout1.getChildAt(0);
            m_SymbolTextView = (TextView)layout1.getChildAt(1);
            m_PriceTextView = (TextView)m_LinearLayout.getChildAt(3);
            LinearLayout layout2 = (LinearLayout)m_LinearLayout.getChildAt(4);
            m_PercentageGained24hTextView = (TextView)layout2.getChildAt(0);
            m_PriceChangeTextView = (TextView)layout2.getChildAt(1);
            /*m_PercentageGained1hTextView = (TextView)m_LinearLayout.getChildAt(3);
            m_PercentageGained24hTextView = (TextView)m_LinearLayout.getChildAt(4);
            m_PercentageGained7dTextView = (TextView)m_LinearLayout.getChildAt(5);*/

        }

        public ViewHolder(ConstraintLayout footerLayout) {
            super(footerLayout);
            m_Footer = footerLayout;
        }

        public ConstraintLayout getFooter() {
            return m_Footer;
        }

        public boolean isFooter() {
            return m_Footer != null;
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
            if(price.isEmpty())
                return;
            price = DisplayFormat.formatString(price);
            m_PriceTextView.setText("$" + price);
        }

        @Override
        public void setPriceChange(String change) {
            m_PriceChangeTextView.setText(change);
        }

        @Override
        public void setPercentageGained(String percentage) {
            m_PercentageGained24hTextView.setText(percentage);
            Double percentChange = Double.parseDouble(percentage.replaceAll("%", "").replaceAll("\\+",""));
            if(percentChange < 0) {
                m_PercentageGained24hTextView.setTextColor(Color.RED);
            }
            else if(percentChange == 0) {
                m_PercentageGained24hTextView.setTextColor(Color.GRAY);
            }
            else {
                m_PercentageGained24hTextView.setTextColor(Color.rgb(100,221,23));
            }
        }

        @Override
        public void setImageBitmap(Bitmap bitmap) {
            m_ImageView.setImageBitmap(bitmap);
        }
    }

    private class ImageLoadingTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            m_InAnimation = new AlphaAnimation(0f, 1f);
            m_InAnimation.setDuration(200);
            m_ProgressBarHolder.bringToFront();
            m_ProgressBarHolder.setAnimation(m_InAnimation);
            m_ProgressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            m_OutAnimation = new AlphaAnimation(1f, 0f);
            m_OutAnimation.setDuration(200);
            m_ProgressBarHolder.setAnimation(m_OutAnimation);
            m_ProgressBarHolder.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... symbols) {
            for(String symbol : symbols) {
                CryptocurrencyInfo.getInstance().loadImage(symbol);
            }
            return null;
        }
    }

    int m_MaxSize = 10;

    FrameLayout m_ProgressBarHolder;
    AlphaAnimation m_InAnimation;
    AlphaAnimation m_OutAnimation;

    public void setOnLaunchFragmentListener(OnLaunchFragmentListener listener) {
        m_OnLaunchFragmentListener = listener;
    }

    public void setProgressBarHolder(FrameLayout progressBarHolder) {
        m_ProgressBarHolder = progressBarHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == m_MaxSize - 1) {
            return 1;
        }
        return 0;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(int startingSize) {
        m_MaxSize = startingSize;
    }

    public void removeViewHolder(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, m_MaxSize - 1);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if(viewType == 1) {
            final ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_footer, parent, false);
            final ViewHolder vh = new ViewHolder(constraintLayout);
            Button button = (Button) constraintLayout.getChildAt(0);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Map.Entry<String, String>> dataset = CryptocurrencyInfo.getInstance().getTopList();
                    int prevSize = m_MaxSize;
                    m_MaxSize = Math.min(m_MaxSize + 10, dataset.size());
                    if(m_MaxSize > 100) {
                        m_MaxSize = 100;
                    }
                    //put this on a separate thread, then notify the main thread that the dataset has changed
                    String[] symbolList = new String[m_MaxSize - prevSize];
                    for(int i = prevSize; i < m_MaxSize; i++) {
                        //final String name = dataset.get(i).getKey();
                        String symbol = dataset.get(i).getValue();
                        symbolList[i - prevSize] = symbol;
                        //CryptocurrencyInfo.getInstance().loadImageAsync(symbol, null);
                    }
                    ImageLoadingTask loadingTask = new ImageLoadingTask();
                    loadingTask.execute(symbolList);
                    notifyDataSetChanged();
                    removeViewHolder(vh.getAdapterPosition());
                }
            });
            return vh;
        }
        // create a new view
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        final ViewHolder vh = new ViewHolder(linearLayout);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(linearLayout.getContext(), DisplayDetailsActivity.class);
                intent.putExtra("Name", vh.m_NameTextView.getText().toString());
                intent.putExtra("Symbol", vh.m_SymbolTextView.getText().toString());
                intent.putExtra("Price", vh.m_PriceTextView.getText().toString());
                intent.putExtra("PercentChange", vh.m_PercentageGained24hTextView.getText().toString());
                intent.putExtra("PriceChange", vh.m_PriceChangeTextView.getText().toString());
                //CryptocurrencyInfo.getInstance().getOldDataset().getCryptocurrencyList().clear();
                linearLayout.getContext().startActivity(intent);*/
                Bundle bundle = new Bundle();
                bundle.putString("Name", vh.m_NameTextView.getText().toString());
                bundle.putString("Symbol", vh.m_SymbolTextView.getText().toString());
                bundle.putString("Price", vh.m_PriceTextView.getText().toString().replaceAll("\\$", ""));
                bundle.putString("PercentChange", vh.m_PercentageGained24hTextView.getText().toString());
                bundle.putString("PriceChange", vh.m_PriceChangeTextView.getText().toString());
                m_OnLaunchFragmentListener.onLaunchFragment(DisplayDetailsFragment.class, bundle, true);
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(position == m_MaxSize - 1) {
            return;
        }

        //HashMap<Integer, Map.Entry<String, String>> dataset = CryptocurrencyInfo.getInstance().getCryptocurrencyNames();
        ArrayList<Map.Entry<String, String>> dataset = CryptocurrencyInfo.getInstance().getTopList();
        if (dataset.get(position) == null) {
            holder.setName("null");
            return;
        }
        final String name = dataset.get(position).getKey();
        final String symbol = dataset.get(position).getValue();
        holder.setName(name);
        holder.setSymbol(symbol);

        holder.m_NumberTextView.setText(String.valueOf(position + 1));
        //migrate this to using getPreviousPriceList
        /*CryptocurrencyDataset oldDataset = CryptocurrencyInfo.getInstance().getOldDataset();
        Cryptocurrency cryptocurrency = oldDataset.getCryptocurrency(name);
        if(cryptocurrency != null) {
            cryptocurrency.setVisible(true);
            holder.setPrice(oldDataset.getCryptocurrency(position).getPrice());
            holder.setPriceChange(oldDataset.getCryptocurrency(position).getPriceChange());
            holder.setPercentageGained(oldDataset.getCryptocurrency(position).getPercentageGained24h());
        }*/

        HashMap<String, Cryptocurrency> priceList = CryptocurrencyInfo.getInstance().getPreviousPriceList();
        //bad thing to do to hardcore -USD
        Cryptocurrency cryptocurrency = priceList.get(symbol + "-USD");
        if(cryptocurrency != null) {
            holder.setPrice(cryptocurrency.getPrice());
            holder.setPriceChange(cryptocurrency.getPriceChange());
            holder.setPercentageGained(cryptocurrency.getPercentageGained24h());
        }

        CryptocurrencyUIUpdateManager.getInstance().addViewHolder(symbol + "-USD", holder);

        boolean loaded = CryptocurrencyInfo.getInstance().isImageLoaded(symbol);
        if(!loaded) {
            //load image view asynchronously (change to name)
            CryptocurrencyInfo.getInstance().loadImageAsync(symbol, new CryptocurrencyInfo.OnLoadImageListener() {
                @Override
                public void onLoad(String symbol, Bitmap bitmap) {
                    Map.Entry<String, Bitmap> data = new HashMap.SimpleEntry<>(symbol + "-USD", bitmap);
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
            Bitmap bitmap = CryptocurrencyInfo.getInstance().getImage(symbol);
            holder.m_ImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder viewHolder) {
        //get the position from the dataset and mark update as disabled
        int position = viewHolder.getAdapterPosition();
        if(viewHolder.isFooter()) {
            return;
        }
        CryptocurrencyDataset oldDataset = CryptocurrencyInfo.getInstance().getOldDataset();
        //getTopList
        HashMap<Integer, Map.Entry<String, String>> dataset = CryptocurrencyInfo.getInstance().getCryptocurrencyNames();
        String name = "";
        //String symbol = "";
        if (dataset.get(position) == null) {
            viewHolder.setName("null");
            return;
        }
        name = dataset.get(position).getKey();
        String symbol = viewHolder.m_SymbolTextView.getText().toString();
        CryptocurrencyUIUpdateManager.getInstance().removeViewHolder(symbol + "-USD");
        /*if(!oldDataset.hasCryptocurrency(name)) {
            Cryptocurrency cryptocurrency = new Cryptocurrency(name, symbol);
            cryptocurrency.setVisible(false);
            cryptocurrency.setDataset(viewHolder.m_PriceTextView.getText().toString()
                    , viewHolder.m_PriceChangeTextView.getText().toString(), "0"
                    , viewHolder.m_PercentageGained24hTextView.getText().toString(), "0");
            oldDataset.addCryptocurrency(position, cryptocurrency);
        }
        else {
            Cryptocurrency cryptocurrency = oldDataset.getCryptocurrency(position);
            cryptocurrency.setVisible(false);
            cryptocurrency.setDataset(viewHolder.m_PriceTextView.getText().toString()
                    , viewHolder.m_PriceChangeTextView.getText().toString(),"0"
                    , viewHolder.m_PercentageGained24hTextView.getText().toString(), "0");
        }*/
        viewHolder.setPrice("");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return m_MaxSize;
        //return CryptocurrencyInfo.getInstance().getDataset().getCryptocurrencyCount();
    }
}
