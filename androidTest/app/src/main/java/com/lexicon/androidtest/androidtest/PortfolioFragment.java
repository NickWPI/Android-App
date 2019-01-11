package com.lexicon.androidtest.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.FrameMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PortfolioFragment extends Fragment {

    public interface FragmentLifecycle {
        void onPauseFragment();
        void onResumeFragment();
    }

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
        initializeData();
    }

    private void initializeData() {
        //test code for database
        PortfolioDao portfolioDao = ApplicationDatabase.getInstance(this.getContext()).portfolioDao();
        if(portfolioDao.count() > 0)
            portfolioDao.deleteAll();
        portfolioDao.insert(new PortfolioEntity(0,0, "Bitcoin", "BTC-USD", "3256.42", "2.61"));
        portfolioDao.insert(new PortfolioEntity(1,1,"Ethereum", "ETH-USD","152.45","42.3"));
        portfolioDao.insert(new PortfolioEntity(2,2, "Litecoin", "LTC-USD","174.23","17.5"));

        //double time = Calendar.getInstance().getTime().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        Date date1 = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date1 = sdf.parse("10/15/2018");
            date2 = sdf.parse("09/11/18");
            date3 = sdf.parse("11/6/18");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TradeHistoryDao tradeHistoryDao = ApplicationDatabase.getInstance(this.getContext()).tradeHistoryDao();
        if(tradeHistoryDao.count() > 0)
            tradeHistoryDao.deleteAll();
        tradeHistoryDao.insert(new TradeHistoryEntity(0, true, "Bitcoin", "BTC-USD", "3256.42", "3256.42","2.61", date1.getTime()));
        tradeHistoryDao.insert(new TradeHistoryEntity(1, false,"Ethereum", "ETH-USD","152.45", "174.32","42.3", date2.getTime()));
        tradeHistoryDao.insert(new TradeHistoryEntity(2, true, "Litecoin", "LTC-USD","174.23", "174.23","17.5", date3.getTime()));
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
        Fragment holdingsFragment = FragmentRepository.getInstance().getFragment(HoldingsFragment.class.getName());
        Fragment tradeFragment = FragmentRepository.getInstance().getFragment(TradeFragment.class.getName());
        Fragment tradeHistoryFragment = FragmentRepository.getInstance().getFragment(TradeHistoryFragment.class.getName());
        //Fragment holdingsSummaryFragment = FragmentRepository.getInstance().getFragment(HoldingsSummaryFragment.class.getName());
        if(holdingsFragment == null || tradeFragment == null) {
            holdingsFragment = new HoldingsFragment();
            tradeFragment = new TradeFragment();
            tradeHistoryFragment = new TradeHistoryFragment();
            //holdingsSummaryFragment = new HoldingsSummaryFragment();
            FragmentRepository.getInstance().addFragment(HoldingsFragment.class.getName(), holdingsFragment);
            FragmentRepository.getInstance().addFragment(TradeFragment.class.getName(), tradeFragment);
            FragmentRepository.getInstance().addFragment(TradeHistoryFragment.class.getName(), tradeHistoryFragment);
            //FragmentRepository.getInstance().addFragment(HoldingsSummaryFragment.class.getName(), holdingsSummaryFragment);
        }
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        //adapter.addFragment(holdingsSummaryFragment, "Summary");
        adapter.addFragment(holdingsFragment, "Holdings");
        adapter.addFragment(tradeFragment, "Trade");
        adapter.addFragment(tradeHistoryFragment, "Trade History");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentPosition = 0;

            @Override
            public void onPageSelected(int position) {
                FragmentLifecycle fragmentToShow = (FragmentLifecycle)adapter.getItem(position);
                fragmentToShow.onResumeFragment();

                FragmentLifecycle fragmentToHide = (FragmentLifecycle)adapter.getItem(currentPosition);
                fragmentToHide.onPauseFragment();

                currentPosition = position;
            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (viewPager.getCurrentItem() == 0) {
                        AppCompatActivity activity = (AppCompatActivity) PortfolioFragment.this.getActivity();
                        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }
        });
    }

    public ViewPager getViewPager() {
        return m_ViewPager;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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
        /*FragmentManager fragmentManager = getChildFragmentManager();
        for(Fragment fragment : fragmentManager.getFragments()) {
            fragment.onOptionsItemSelected(item);
        }*/
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
