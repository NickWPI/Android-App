package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class RecyclerViewDividerDecoration extends RecyclerView.ItemDecoration  {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable m_Divider;
    private Context m_Context;
    //in pixels
    private int m_PaddingLeft;
    private int m_PaddingRight;

    /**
     * Default divider will be used
     */
    public RecyclerViewDividerDecoration(Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        m_Divider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
        m_Context = context;
        m_PaddingLeft = this.convertDpToPixel(10, context);
        m_PaddingRight = this.convertDpToPixel(10, context);
    }

    /**
     * Custom divider will be used
     */
    public RecyclerViewDividerDecoration(Context context, int resId) {
        m_Divider = ContextCompat.getDrawable(context, resId);
        m_Context = context;
        m_PaddingLeft = this.convertDpToPixel(10, context);
        m_PaddingRight = this.convertDpToPixel(10, context);
    }

    private int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int)(dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + m_PaddingLeft;
        int right = parent.getWidth() - (parent.getPaddingRight() + m_PaddingRight);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + m_Divider.getIntrinsicHeight();

            m_Divider.setBounds(left, top, right, bottom);
            m_Divider.draw(c);
        }
    }
}
