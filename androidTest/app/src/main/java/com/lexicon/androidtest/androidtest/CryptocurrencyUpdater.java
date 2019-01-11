package com.lexicon.androidtest.androidtest;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptocurrencyUpdater {
    //handler to handle pauses on updating
    Handler m_Handler;

    enum Status {
        START,
        STOP,
        RESUME,
        PAUSE
    }

    CryptocurrencyDataset m_Dataset;

    Thread m_Thread;

    public CryptocurrencyUpdater() {
        //initialize everything
        m_Dataset = new CryptocurrencyDataset();
    }

    public void initialize() {

    }

    public void run() {
        m_Thread = new Thread(new Updater());
        m_Thread.start();
    }

    public void stop() {
        m_Thread.interrupt();
    }

    private class Updater implements Runnable {
        boolean m_Running;

        public Updater() {
        }

        public synchronized void setRunning(boolean running) {
            m_Running = running;
        }

        public synchronized boolean isRunning() {
            return m_Running;
        }

        @Override
        public void run() {
                try {
                    while (true) {
                        Collection<String> activeList = CryptocurrencyUIUpdateManager.getInstance().getActiveCryptocurrencyList();
                        if(activeList.size() == 0) {
                            continue;
                        }
                        ArrayList<String> symbolList = new ArrayList<String>();
                        synchronized(CryptocurrencyInfo.getInstance().getDataset()) {
                            synchronized (activeList) {
                                for (String c : activeList) {
                                    //Cryptocurrency cryptocurrency = CryptocurrencyInfo.getInstance().getDataset().getCryptocurrency(c);
                                    //String symbol = cryptocurrency.getSymbol();
                                    Log.d("CryptocurrencyUpdater", c);
                                    symbolList.add(c);
                                }
                            }
                            String[] s = new String[symbolList.size()];
                            symbolList.toArray(s);
                            Log.d("CryptocurrencyUpdater", String.valueOf(s.length));
                            List<Cryptocurrency> cryptocurrencyList = CryptocurrencyInfo.getInstance().getPriceByTradeStrings(s);
                            Message message = new Message();
                            CryptocurrencyUIUpdateManager.DataObject msgObj = new CryptocurrencyUIUpdateManager.DataObject();
                            msgObj.m_Data = cryptocurrencyList;
                            msgObj.m_UpdateOptions = CryptocurrencyUIUpdateManager.UpdateOptions.UPDATE_DATA;
                            message.obj = msgObj;
                            CryptocurrencyUIUpdateManager.getInstance().getHandler().sendMessage(message);
                        }
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }
    }
}
