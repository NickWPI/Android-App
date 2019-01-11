package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SearchListAdapter extends ArrayAdapter {
    private List<String> m_Data;
    private Context m_Context;
    private int m_Resource;

    public SearchListAdapter(@NonNull Context context, int resource, @NonNull List<String> data) {
        super(context, resource, data);
        m_Data = data;
        m_Context = context;
        m_Resource = resource;
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(m_Resource, parent, false);
        }

        TextView resultItem = (TextView) view.findViewById(android.R.id.text1);
        resultItem.setText(getItem(position));
        return view;
    }

    @Override
    public int getCount() {
        return m_Data.size();
    }

    @Override
    public String getItem(int position) {
        return m_Data.get(position);
    }
}
