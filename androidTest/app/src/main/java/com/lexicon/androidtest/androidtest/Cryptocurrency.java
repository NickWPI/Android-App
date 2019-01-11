package com.lexicon.androidtest.androidtest;

import java.util.ArrayList;
import java.util.List;

public class Cryptocurrency {
    private String m_Name;
    private String m_Symbol;
    private String m_Price;
    private String m_PercentageGained1h;
    private String m_PercentageGained24h;
    private String m_PercentageGained7d;
    private String m_Change;

    List<Listener> m_DatasetListeners;

    boolean m_Dirty;
    boolean m_EnableUpdate;
    boolean m_Visible;

    public static class Listener {
        public void onDatasetChanged(Cryptocurrency dataset) {}
    }

    public Cryptocurrency(String name, String symbol) {
        m_DatasetListeners = new ArrayList<Listener>();
        m_Name = name;
        m_Symbol = symbol;
        m_PercentageGained1h = "0%";
        m_PercentageGained24h = "0%";
        m_PercentageGained7d = "0%";
        m_Change = "0";
    }

    public Cryptocurrency(String name, String symbol, String price,
                          String percentageGained1h, String percentageGained24h, String percentageGained7d) {
        m_DatasetListeners = new ArrayList<Listener>();
        m_Name = name;
        m_Symbol = symbol;
        m_PercentageGained1h = percentageGained1h;
        m_PercentageGained24h = percentageGained24h;
        m_PercentageGained7d = percentageGained7d;
    }

    public void addListener(Listener listener) {
        m_DatasetListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        m_DatasetListeners.remove(listener);
    }

    public void notifyListeners() {
        for(Listener listener : m_DatasetListeners) {
            listener.onDatasetChanged(this);
        }
    }

    //set true for when the list are visible and false when they are not
    public void setEnableUpdate(boolean enableUpdate) {
        m_EnableUpdate = enableUpdate;
    }

    public boolean getEnableUpdate() {
        return m_EnableUpdate;
    }

    public void markDirty(boolean dirty) {
        m_Dirty = dirty;
    }

    public boolean isDirty() {
        return m_Dirty;
    }

    public void setVisible(boolean visible) {
        m_Visible = visible;
    }

    public boolean isVisible() {
        return m_Visible;
    }

    public void setDataset(String price, String change, String percentageGained1h, String percentageGained24h, String percentageGained7d) {
        m_Price = price;
        m_Change =change;
        m_PercentageGained1h = percentageGained1h;
        m_PercentageGained24h = percentageGained24h;
        m_PercentageGained7d = percentageGained7d;
        notifyListeners();
        markDirty(true);
    }

    public String getName() {
        markDirty(false);
        return m_Name;
    }

    public String getSymbol() {
        markDirty(false);
        return m_Symbol;
    }

    public String getPrice() {
        markDirty(false);
        return m_Price;
    }

    public String getPriceChange() {
        return m_Change;
    }

    public String getPercentageGained1h() {
        markDirty(false);
        return m_PercentageGained1h;
    }

    public String getPercentageGained24h() {
        markDirty(false);
        return m_PercentageGained24h;
    }

    public String getPercentageGained7d() {
        markDirty(false);
        return m_PercentageGained7d;
    }
}
