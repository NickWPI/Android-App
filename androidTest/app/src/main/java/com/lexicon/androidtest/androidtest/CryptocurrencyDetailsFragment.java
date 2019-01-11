package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CryptocurrencyDetailsFragment extends Fragment {

    private OnDrawerSelectedListener m_DrawerListener;
    private OnLaunchFragmentListener m_FragmentListener;

    private TabLayout m_TabLayout;
    private ViewPager m_ViewPager;

    private boolean m_EnableNavigationDrawer = true;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_portfolio, parent, false);
        setHasOptionsMenu(true);

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(myToolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        m_ViewPager = view.findViewById(R.id.view_pager);
        setupViewPager(m_ViewPager);
        m_TabLayout = view.findViewById(R.id.tabs);
        m_TabLayout.setupWithViewPager(m_ViewPager);

        return view;
    }

    private void setupViewPager(final ViewPager viewPager) {

    }

    public void setEnableNavigationDrawer(boolean enableNavigationDrawer) {
        m_EnableNavigationDrawer = enableNavigationDrawer;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(m_EnableNavigationDrawer)
                    m_DrawerListener.onDrawerSelected(item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> m_FragmentList = new ArrayList<Fragment>();
        private final List<String> m_FragmentTitleList = new ArrayList<String>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return m_FragmentList.get(position);
        }

        @Override
        public int getCount() {
            return m_FragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            m_FragmentList.add(fragment);
            m_FragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return m_FragmentTitleList.get(position);
        }
    }
}
