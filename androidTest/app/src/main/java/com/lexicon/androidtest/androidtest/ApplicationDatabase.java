package com.lexicon.androidtest.androidtest;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {WatchlistEntity.class, PortfolioEntity.class, TradeHistoryEntity.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {
    public abstract WatchlistDao watchlistDao();
    public abstract PortfolioDao portfolioDao();
    public abstract TradeHistoryDao tradeHistoryDao();

    private static String DB_NAME = "user_database.db";

    private static ApplicationDatabase m_Instance;

    public static ApplicationDatabase getInstance(Context context) {
        if (m_Instance == null) {
            m_Instance = Room.databaseBuilder(
                    context.getApplicationContext(), ApplicationDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .build();
        }
        return m_Instance;
    }
}
