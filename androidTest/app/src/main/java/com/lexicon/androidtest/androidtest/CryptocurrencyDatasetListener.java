package com.lexicon.androidtest.androidtest;

import android.os.Bundle;
import android.os.Message;

public class CryptocurrencyDatasetListener extends Cryptocurrency.Listener {
    RecyclerViewAdapter.ViewHolder m_ViewHolder;

    public static class UIDataPair {
        public UIDataPair(RecyclerViewAdapter.ViewHolder viewHolder, Cryptocurrency cryptocurrency) {
            m_ViewHolder = viewHolder;
            m_Cryptocurrency = cryptocurrency;
        }

        public RecyclerViewAdapter.ViewHolder m_ViewHolder;
        public Cryptocurrency m_Cryptocurrency;
    }

    public CryptocurrencyDatasetListener(RecyclerViewAdapter.ViewHolder viewHolder) {
        m_ViewHolder = viewHolder;
    }

    @Override
    public void onDatasetChanged(Cryptocurrency dataset) {
       UIDataPair data = new UIDataPair(m_ViewHolder, dataset);
       Message message = new Message();
       message.obj = data;
       CryptocurrencyUIUpdateManager.getInstance().getHandler().sendMessage(message);
    }
}
