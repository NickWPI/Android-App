package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewItemOffsetDecoration extends RecyclerView.ItemDecoration {
    private int spacing;

    public RecyclerViewItemOffsetDecoration() {
        this.spacing = 3;
    }

    public RecyclerViewItemOffsetDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = spacing;
    }
}
