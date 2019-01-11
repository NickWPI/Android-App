package com.lexicon.androidtest.androidtest;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tradeHistory")
public class TradeHistoryEntity {
    @PrimaryKey
    private int id;

    @ColumnInfo(name="transaction_type")
    private boolean transactionType;

    @ColumnInfo(name="cryptocurrency_name")
    private String name;

    @ColumnInfo(name="cryptocurrency_symbol")
    private String symbol;

    @ColumnInfo(name="purchase_price")
    private String purchasePrice;

    @ColumnInfo(name="sell_price")
    private String sellPrice;

    @ColumnInfo(name="quantity")
    private String quantity;

    @ColumnInfo(name="date")
    private long date;

    //buy
    /*public TradeHistoryEntity(int id, boolean transactionType, String name, String symbol, String purchasePrice, String quantity, long date) {
        setId(id);
        setName(name);
        setTransactionType(transactionType);
        setSymbol(symbol);
        setPurchasePrice(purchasePrice);
        setSellPrice(purchasePrice);
        setQuantity(quantity);
        setDate(date);
    }*/

    //sell
    public TradeHistoryEntity(int id, boolean transactionType, String name, String symbol, String purchasePrice, String sellPrice, String quantity, long date) {
        setId(id);
        setName(name);
        setTransactionType(transactionType);
        setSymbol(symbol);
        setPurchasePrice(purchasePrice);
        setSellPrice(sellPrice);
        setQuantity(quantity);
        setDate(date);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setTransactionType(boolean transactionType) {
        this.transactionType = transactionType;
    }

    public boolean getTransactionType() {
        return this.transactionType;
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

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPurchasePrice() {
        return this.purchasePrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getSellPrice() {
        return this.sellPrice;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDate() {
        return this.date;
    }
}
