package com.lexicon.androidtest.androidtest;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WatchlistDao {

    @Query("SELECT * FROM watchlist ORDER BY position ASC")
    List<WatchlistEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WatchlistEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(WatchlistEntity... entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(WatchlistEntity entity);

    @Delete
    void delete(WatchlistEntity entity);

    @Query("DELETE FROM watchlist")
    void deleteAll();

    @Query("SELECT COUNT(*) from watchlist")
    int count();
}
