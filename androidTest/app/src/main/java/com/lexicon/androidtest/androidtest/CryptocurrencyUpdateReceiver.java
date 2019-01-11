package com.lexicon.androidtest.androidtest;

import android.graphics.Bitmap;

public interface CryptocurrencyUpdateReceiver {
    void setName(String name);
    void setSymbol(String symbol);
    void setPrice(String price);
    void setPriceChange(String change);
    void setPercentageGained(String percentage);
    void setImageBitmap(Bitmap bitmap);
}
