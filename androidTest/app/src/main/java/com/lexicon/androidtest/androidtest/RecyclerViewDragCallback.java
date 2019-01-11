package com.lexicon.androidtest.androidtest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class RecyclerViewDragCallback extends ItemTouchHelper.Callback {

    public interface OnDragAndDropListener {
        void onViewMoved(int oldPosition, int newPosition);
        void onViewSwiped(int position, int direction);
    }

    private OnDragAndDropListener m_Listener;
    boolean m_EnableSwipe;
    boolean m_EnableMove;

    public RecyclerViewDragCallback(OnDragAndDropListener listener) {
        m_Listener = listener;
        m_EnableSwipe = false;
        m_EnableMove = true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = m_EnableMove ? (ItemTouchHelper.UP | ItemTouchHelper.DOWN) : 0;
        int swipeFlags = m_EnableSwipe ? (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) : 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        m_Listener.onViewMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        m_Listener.onViewSwiped(viewHolder.getAdapterPosition(), direction);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }
}
