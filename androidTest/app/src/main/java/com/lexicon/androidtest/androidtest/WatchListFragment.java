package com.lexicon.androidtest.androidtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WatchListFragment extends Fragment implements OnSearchDetailsListener {

    private RecyclerView m_RecyclerView;
    private WatchlistRecyclerViewAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    private OnDrawerSelectedListener m_DrawerListener;
    private OnLaunchFragmentListener m_FragmentListener;

    private List<String> m_DeletionList;

    private List<WatchlistEntity> m_WatchlistEntities;
    private boolean m_WatchlistModified;

    private WatchlistEntity m_RecentlyAdded;

    boolean m_UseItemRemovalMenu = false;

    public WatchListFragment() {
        m_DeletionList = new ArrayList<String>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            m_DrawerListener = (OnDrawerSelectedListener) context;
            m_FragmentListener = (OnLaunchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDrawerSelectedListener and OnLaunchFragmentListener");
        }
    }

    private void animationCheckBoxVisible(final View view) {
    }

    private void animateCheckBoxGone(final View view) {
        //animatelayoutchanges=true basically takes care of this
        final int originalWidth = view.getWidth();
        ValueAnimator animation = ValueAnimator.ofInt(originalWidth, 0);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                int animatedValue = (int)updatedAnimation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = animatedValue;
                view.setLayoutParams(layoutParams);
            }
        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = originalWidth;
                view.setLayoutParams(layoutParams);
            }
        });
        animation.setDuration(500);
        animation.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_watchlist, parent, false);
        setHasOptionsMenu(true);

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(myToolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //test code
        WatchlistDao watchlistDao = ApplicationDatabase.getInstance(this.getContext()).watchlistDao();
        if(m_WatchlistEntities == null)
            m_WatchlistEntities = watchlistDao.getAll();
        if(m_RecentlyAdded != null) {
            m_WatchlistEntities.add(m_RecentlyAdded);
            m_RecentlyAdded = null;
        }
        for(WatchlistEntity entity : m_WatchlistEntities) {
            Log.d("DATABASE", entity.getName());
        }

        m_RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this.getContext());
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        m_Adapter = new WatchlistRecyclerViewAdapter(m_WatchlistEntities, new WatchlistRecyclerViewAdapter.OnLongClickListener() {

            @Override
            public void onLongClick() {
                int count = m_Adapter.getItemCount();
                for(int i = 0; i < count; i++) {
                    WatchlistRecyclerViewAdapter.ViewHolder viewHolder
                            = (WatchlistRecyclerViewAdapter.ViewHolder)m_RecyclerView.findViewHolderForAdapterPosition(i);
                    CheckBox checkBox = viewHolder.itemView.findViewById(R.id.check_box);
                    checkBox.setVisibility(View.VISIBLE);
                    //animationCheckBoxVisible(checkBox);
                    ImageView imageView = viewHolder.itemView.findViewById(R.id.image_view);
                    imageView.setVisibility(View.VISIBLE);
                }
                //setup item removal menu
                AppCompatActivity activity = (AppCompatActivity)WatchListFragment.this.getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("Remove Items");
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                m_UseItemRemovalMenu = true;
                activity.invalidateOptionsMenu();
            }
        });
        m_Adapter.setOnCheckBoxClickedListener(new WatchlistRecyclerViewAdapter.OnCheckBoxClickedListener() {
            @Override
            public void onClicked(WatchlistRecyclerViewAdapter.ViewHolder viewHolder) {
                String name = viewHolder.m_NameTextView.getText().toString();
                CheckBox checkBox = viewHolder.itemView.findViewById(R.id.check_box);
                boolean checked = checkBox.isChecked();
                if(checked) {
                    m_DeletionList.add(name);
                }
                else {
                    m_DeletionList.remove(name);
                }
            }
        });
        m_Adapter.setOnLaunchFragmentListener(m_FragmentListener);
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(5));
        m_RecyclerView.setAdapter(m_Adapter);

        RecyclerViewDragCallback dragCallback = new RecyclerViewDragCallback(m_Adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(dragCallback);
        touchHelper.attachToRecyclerView(m_RecyclerView);
        m_Adapter.setItemTouchHelper(touchHelper);

        /*FloatingActionButton floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new FloatingActionButton.OnClickListener() {

            @Override
            public void onClick(View view) {
                cancelItemRemovalMenu();
                Bundle bundle = new Bundle();
                bundle.putString("FragmentName", WatchListFragment.class.getName());
                m_FragmentListener.onLaunchFragment(SearchDetailsFragment.class, bundle, true);
            }
        });*/

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(m_WatchlistModified) {
            //save data to watchlist
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if(m_UseItemRemovalMenu) {
            inflater.inflate(R.menu.item_remove_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
        else {
            inflater.inflate(R.menu.list_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    private void cancelItemRemovalMenu() {
        m_DeletionList.clear();
        int count = m_Adapter.getItemCount();
        for(int i = 0; i < count; i++) {
            WatchlistRecyclerViewAdapter.ViewHolder viewHolder
                    = (WatchlistRecyclerViewAdapter.ViewHolder)m_RecyclerView.findViewHolderForAdapterPosition(i);
            if(viewHolder == null)
                continue;
            CheckBox checkBox = viewHolder.itemView.findViewById(R.id.check_box);
            if(checkBox.isChecked())
                checkBox.setChecked(false);
            checkBox.setVisibility(View.GONE);
            //animateCheckBoxGone(checkBox);
            ImageView imageView = viewHolder.itemView.findViewById(R.id.image_view);
            imageView.setVisibility(View.GONE);
        }
        m_Adapter.cancelLongClick();
        //setup item removal menu
        AppCompatActivity activity = (AppCompatActivity)WatchListFragment.this.getActivity();
        m_UseItemRemovalMenu = false;
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Watchlist");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        activity.invalidateOptionsMenu();
    }

    private void uncheckAllBoxes() {
        int count = m_Adapter.getItemCount();
        for(int i = 0; i < count; i++) {
            RecyclerView.ViewHolder viewHolder = m_RecyclerView.findViewHolderForAdapterPosition(i);
            if(viewHolder == null) {
                continue;
            }
            CheckBox checkBox = viewHolder.itemView.findViewById(R.id.check_box);
            if(checkBox.isChecked())
                checkBox.setChecked(false);
        }
    }

    public void launchSearch() {
        cancelItemRemovalMenu();
        Bundle bundle = new Bundle();
        bundle.putString("FragmentName", WatchListFragment.class.getName());
        m_FragmentListener.onLaunchFragment(SearchDetailsFragment.class, bundle, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(m_UseItemRemovalMenu) {
                    cancelItemRemovalMenu();
                }
                else {
                    m_DrawerListener.onDrawerSelected(item.getItemId());
                    return true;
                }
            case R.id.cancel:
                cancelItemRemovalMenu();
                break;
            case R.id.delete:
                uncheckAllBoxes();
                m_Adapter.removeWatchlistEntities(m_DeletionList);
                Toast.makeText(this.getContext(), "Removed " + m_DeletionList.size() + " items" , Toast.LENGTH_SHORT).show();
                m_DeletionList.clear();
                //cancelItemRemovalMenu();
                break;
            case R.id.edit:
                m_Adapter.startLongClick();
                break;
            case R.id.add:
                launchSearch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchResult(Fragment searchFragment, String name, String symbol) {
        //make sure this element doesn't already exist
        if(m_Adapter.hasWatchlistEntity(symbol)) {
            return;
        }
        int order = m_Adapter.getItemCount();
        WatchlistEntity entity = new WatchlistEntity(order, order, name, symbol);
        m_RecentlyAdded = entity;
    }
}
