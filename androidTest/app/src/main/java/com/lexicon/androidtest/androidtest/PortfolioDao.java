package com.lexicon.androidtest.androidtest;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PortfolioDao {

    @Query("SELECT * FROM portfolio ORDER BY position ASC")
    List<PortfolioEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PortfolioEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(PortfolioEntity... entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(PortfolioEntity entity);

    @Delete
    void delete(PortfolioEntity entity);

    @Query("DELETE FROM portfolio")
    void deleteAll();

    @Query("SELECT COUNT(*) from portfolio")
    int count();
}