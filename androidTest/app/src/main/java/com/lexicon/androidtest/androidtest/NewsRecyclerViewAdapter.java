package com.lexicon.androidtest.androidtest;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

    private int m_AdapterSize;

    private Handler m_Handler;

    private HashMap<Integer, Drawable> m_SavedDrawables;

    public class DataObject {
        public ImageView m_ImageView;
        public Drawable m_Drawable;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout m_Layout;

        TextView m_TitleTextView;
        TextView m_BodyTextView;
        ImageView m_ImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            m_Layout = (ConstraintLayout)itemView;
            m_TitleTextView = m_Layout.findViewById(R.id.title_text_view);
            m_BodyTextView = m_Layout.findViewById(R.id.body_text_view);
            m_ImageView = m_Layout.findViewById(R.id.image_view);
        }
    }

    public NewsRecyclerViewAdapter() {
        m_AdapterSize = CryptocurrencyInfo.getInstance().getNewsArticles().size();
        m_SavedDrawables = new HashMap<>();
        m_Handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                DataObject dataObj = (DataObject)inputMessage.obj;
                dataObj.m_ImageView.setImageDrawable(dataObj.m_Drawable);
            }
        };
    }

    private int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimaryDark });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_recycler_view_item, parent, false);
        final ViewHolder vh = new ViewHolder(layout);
        vh.itemView.setOnClickListener(new ConstraintLayout.OnClickListener() {

            @Override
            public void onClick(View view) {
                int position = vh.getAdapterPosition();
                List<CryptocurrencyInfo.NewsArticle> articles = CryptocurrencyInfo.getInstance().getNewsArticles();
                CryptocurrencyInfo.NewsArticle article = articles.get(position);
                String url = article.m_Url;
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getPrimaryColor(parent.getContext()));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl((Activity)parent.getContext(), Uri.parse(url));
            }
        });
        return vh;
    }

    public Drawable loadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {
        List<CryptocurrencyInfo.NewsArticle> articles = CryptocurrencyInfo.getInstance().getNewsArticles();
        final CryptocurrencyInfo.NewsArticle article = articles.get(position);
        if(!m_SavedDrawables.containsKey(position)) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    InputStream is = null;
                    try {
                        is = (InputStream) new URL(article.m_ImageUrl).getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    DataObject dataObject = new DataObject();
                    Drawable drawable = Drawable.createFromStream(is, "src name");
                    m_SavedDrawables.put(position, drawable);
                    dataObject.m_ImageView = holder.m_ImageView;
                    dataObject.m_Drawable = drawable;
                    message.obj = dataObject;
                    NewsRecyclerViewAdapter.this.m_Handler.sendMessage(message);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        else {
            Drawable drawable = m_SavedDrawables.get(position);
            holder.m_ImageView.setImageDrawable(drawable);
        }

        holder.m_TitleTextView.setText(article.m_Title);
        holder.m_BodyTextView.setText(Html.fromHtml(article.m_Body));
    }

    @Override
    public void onViewRecycled(ViewHolder viewHolder) {
        viewHolder.m_ImageView.setImageDrawable(null);
    }

    @Override
    public int getItemCount() {
        return m_AdapterSize;
    }
}
