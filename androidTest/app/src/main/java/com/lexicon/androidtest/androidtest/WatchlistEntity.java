package com.lexicon.androidtest.androidtest;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "watchlist")
public class WatchlistEntity {
    @PrimaryKey
    private int id;

    //define ordering of entries
    @ColumnInfo(name="position")
    private int order;

    @ColumnInfo(name="cryptocurrency_name")
    private String name;

    @ColumnInfo(name="cryptocurrency_symbol")
    private String symbol;

    public WatchlistEntity(int id, int order, String name, String symbol) {
        setId(id);
        setOrder(order);
        setName(name);
        setSymbol(symbol);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
