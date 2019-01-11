package com.lexicon.androidtest.androidtest;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "portfolio")
public class PortfolioEntity {
    @PrimaryKey
    private int id;

    @ColumnInfo(name="position")
    private int order;

    @ColumnInfo(name="cryptocurrency_name")
    private String name;

    @ColumnInfo(name="cryptocurrency_symbol")
    private String symbol;

    @ColumnInfo(name="purchase_price")
    private String purchasePrice;

    @ColumnInfo(name="quantity")
    private String quantity;

    public PortfolioEntity(int id, int order, String name, String symbol, String purchasePrice, String quantity) {
        setId(id);
        setOrder(order);
        setName(name);
        setSymbol(symbol);
        setPurchasePrice(purchasePrice);
        setQuantity(quantity);
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

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPurchasePrice() {
        return this.purchasePrice;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return this.quantity;
    }
}
