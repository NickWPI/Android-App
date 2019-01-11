package com.lexicon.androidtest.androidtest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CryptocurrencyDataset {
    LinkedHashMap<Integer, Cryptocurrency> m_Cryptocurrency;

    public CryptocurrencyDataset() {
        m_Cryptocurrency = new LinkedHashMap<Integer, Cryptocurrency>();
    }

    public void adjustDataset() {
        Object[] entrySet = m_Cryptocurrency.entrySet().toArray();
        Arrays.sort(entrySet, new Comparator() {
            @Override
            public int compare(Object o, Object t1) {
                return ((Map.Entry<Integer, Cryptocurrency>) o).getKey()
                        .compareTo(((Map.Entry<Integer, Cryptocurrency>) t1).getKey());
            }
        });
        LinkedHashMap<Integer, Cryptocurrency> newMap = new LinkedHashMap<Integer, Cryptocurrency>();
        int count = 0;
        for(Object o : entrySet) {
            newMap.put(count, ((Map.Entry<Integer, Cryptocurrency>)o).getValue());
            count++;
        }
        m_Cryptocurrency = newMap;
    }

    public void addCryptocurrency(Integer sortOrder, Cryptocurrency crypto) {
        m_Cryptocurrency.put(sortOrder, crypto);
    }

    public Cryptocurrency getCryptocurrency(String name) {
        for(Cryptocurrency cryptocurrency : m_Cryptocurrency.values()) {
            if(cryptocurrency.getName().equalsIgnoreCase(name)) {
                return cryptocurrency;
            }
        }

        return null;
    }

    public Cryptocurrency getCryptocurrencyBySymbol(String symbol) {
        for(Cryptocurrency cryptocurrency : m_Cryptocurrency.values()) {
            if(cryptocurrency.getSymbol().equalsIgnoreCase(symbol)) {
                return cryptocurrency;
            }
        }

        return null;
    }

    public Cryptocurrency getCryptocurrency(int index) {
        return m_Cryptocurrency.get(index);
    }

    public boolean hasCryptocurrency(String name) {
        Cryptocurrency cryptocurrency = getCryptocurrency(name);
        if(cryptocurrency == null)
            return false;
        return true;
    }

    public int getCryptocurrencyCount() {
        return m_Cryptocurrency.size();
    }

    public Collection<Cryptocurrency> getCryptocurrencyList() {
        return m_Cryptocurrency.values();
    }

    public void addListener(Cryptocurrency.Listener listener) {
        for(Cryptocurrency crypto : m_Cryptocurrency.values()) {
            crypto.addListener(listener);
        }
    }

    public void removeListener(Cryptocurrency.Listener listener) {
        for(Cryptocurrency crypto : m_Cryptocurrency.values()) {
            crypto.removeListener(listener);
        }
    }

    public void notifyListeners() {
        for(Cryptocurrency crypto : m_Cryptocurrency.values()) {
            crypto.notifyListeners();
        }
    }
}
