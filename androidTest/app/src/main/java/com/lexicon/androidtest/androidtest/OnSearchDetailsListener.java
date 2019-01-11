package com.lexicon.androidtest.androidtest;

import android.support.v4.app.Fragment;

public interface OnSearchDetailsListener {
    void onSearchResult(Fragment searchFragment, String name, String symbol);
}
