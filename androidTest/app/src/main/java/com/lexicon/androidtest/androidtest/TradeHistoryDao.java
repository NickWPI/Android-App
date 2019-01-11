package com.lexicon.androidtest.androidtest;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TradeHistoryDao {
    @Query("SELECT * FROM tradeHistory ORDER BY date DESC")
    List<TradeHistoryEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TradeHistoryEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(TradeHistoryEntity... entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(TradeHistoryEntity entity);

    @Delete
    void delete(TradeHistoryEntity entity);

    @Query("DELETE FROM tradeHistory")
    void deleteAll();

    @Query("SELECT COUNT(*) from tradeHistory")
    int count();
}
