package com.lexicon.androidtest.androidtest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

//provides a base class for view holders that need to be updated using
//the cryptocurrencyUIUpdateManager
public class UpdatableViewHolder extends RecyclerView.ViewHolder {
    public UpdatableViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
