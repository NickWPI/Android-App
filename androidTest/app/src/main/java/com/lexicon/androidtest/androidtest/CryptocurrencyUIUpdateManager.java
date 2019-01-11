package com.lexicon.androidtest.androidtest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CryptocurrencyUIUpdateManager {
    private static class Holder {
        private static CryptocurrencyUIUpdateManager manager = new CryptocurrencyUIUpdateManager();
    }

    final HashMap<String, CryptocurrencyUpdateReceiver> m_ViewHolders;
    final ArrayList<String> m_ActiveCryptocurrencyList; //to prevent the concurrent exception thing

    Handler m_Handler;

    public enum UpdateOptions {
        UPDATE_DATA,
        UPDATE_IMAGE
    }

    public static class DataObject {
        UpdateOptions m_UpdateOptions;
        Object m_Data;
    }

    private CryptocurrencyUIUpdateManager() {
        m_ViewHolders = new HashMap<String, CryptocurrencyUpdateReceiver>();
        m_ActiveCryptocurrencyList = new ArrayList<String>();
        m_Handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                DataObject dataObj = (DataObject) inputMessage.obj;
                switch(dataObj.m_UpdateOptions) {
                    case UPDATE_DATA:
                        List<Cryptocurrency> cryptocurrencyList = ( List<Cryptocurrency>)dataObj.m_Data;
                        for(Cryptocurrency cryptocurrency : cryptocurrencyList) {
                            Log.d("DEBUG", cryptocurrency.getName());
                            //if(cryptocurrency.isDirty()) {
                            CryptocurrencyUpdateReceiver viewHolder = getViewHolder(cryptocurrency.getSymbol());
                            //no clue why this would be null
                            if(viewHolder == null)
                                continue;
                            viewHolder.setPrice(cryptocurrency.getPrice());
                            //viewHolder.setPercentageGained1hTextView(cryptocurrency.getPercentageGained1h());
                            viewHolder.setPriceChange(cryptocurrency.getPriceChange());
                            viewHolder.setPercentageGained(cryptocurrency.getPercentageGained24h());
                        }
                        break;
                    case UPDATE_IMAGE:
                        Map.Entry<String, Bitmap> image = (Map.Entry<String, Bitmap>)dataObj.m_Data;
                        String name = image.getKey();
                        Bitmap bitmap = image.getValue();
                        CryptocurrencyUpdateReceiver viewHolder = getViewHolder(name);
                        if(bitmap != null && viewHolder != null)
                            viewHolder.setImageBitmap(bitmap);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public Collection<String> getActiveCryptocurrencyList() {
        return m_ActiveCryptocurrencyList;
    }

    public Collection<CryptocurrencyUpdateReceiver> getActiveViewHolderList() {
        return m_ViewHolders.values();
    }

    public HashMap<String, CryptocurrencyUpdateReceiver> getViewHolderMap() {
        return m_ViewHolders;
    }

    public void setActiveCryptocurrency(String name) {
        synchronized (m_ActiveCryptocurrencyList) {
            if(m_ActiveCryptocurrencyList.contains(name)) {
                return;
            }

            m_ActiveCryptocurrencyList.add(name);
        }
    }

    public void addViewHolder(String name, CryptocurrencyUpdateReceiver viewHolder) {
        m_ViewHolders.put(name, viewHolder);
        setActiveCryptocurrency(name);
    }

    public boolean isActive(String name) {
        return m_ViewHolders.containsKey(name);
    }

    public boolean isActive1(String name) {
        return m_ActiveCryptocurrencyList.contains(name);
    }

    public CryptocurrencyUpdateReceiver getViewHolder(String name) {
        return m_ViewHolders.get(name);
    }

    public boolean removeViewHolder(String name) {
        m_ViewHolders.remove(name);
        synchronized (m_ActiveCryptocurrencyList) {
            return m_ActiveCryptocurrencyList.remove(name);
        }
    }

    public Handler getHandler() {
        return m_Handler;
    }

    public static CryptocurrencyUIUpdateManager getInstance() {
        return Holder.manager;
    }
}
