package com.lexicon.androidtest.androidtest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements OnDrawerSelectedListener, OnLaunchFragmentListener {

    private DrawerLayout m_DrawerLayout;
    private RecyclerView m_RecyclerView;
    private RecyclerViewAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    private Class<? extends Fragment> m_CurrentFragmentToSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CryptocurrencyUpdater updater = new CryptocurrencyUpdater();
        //Thread thread = new Thread(updater);

        /*m_RecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        m_RecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        m_LayoutManager = new PreCachingLayoutManager(this, 1200);
        m_RecyclerView.setLayoutManager(m_LayoutManager);

        String[] dataset = {
           "apple", "banana", "cherry",
           "donut", "ectoplasm", "fin",
           "gyroscope", "highness", "igloo",
           "july", "kangaroo", "lypoma"
        };

        CryptocurrencyInfo.getInstance().generateCryptocurrencyList();

        CryptocurrencyUpdater updater = new CryptocurrencyUpdater();
        updater.run();

        // specify an adapter (see also next example)
        m_Adapter = new RecyclerViewAdapter(dataset);
        m_RecyclerView.addItemDecoration(new RecyclerViewDividerDecoration(
                m_RecyclerView.getContext(), R.drawable.recycler_view_divider));
        m_RecyclerView.addItemDecoration(new RecyclerViewItemOffsetDecoration(5));
        m_RecyclerView.setAdapter(m_Adapter);
        FrameLayout progressBarHolder = (FrameLayout)findViewById(R.id.progress_bar_holder);
        m_Adapter.setProgressBarHolder(progressBarHolder);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);*/

        NavigationView navigationView = findViewById(R.id.nav_view);
        m_DrawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                m_DrawerLayout.closeDrawers();

                switch(menuItem.getItemId()) {
                    case R.id.nav_camera:
                        m_CurrentFragmentToSet = CryptocurrencyListFragment.class;
                        break;
                    case R.id.nav_gallery:
                        m_CurrentFragmentToSet = WatchListFragment.class;
                        break;
                    case R.id.nav_slideshow:
                        m_CurrentFragmentToSet = PortfolioFragment.class;
                        break;
                    case R.id.nav_manage:
                        m_CurrentFragmentToSet = NewsFragment.class;
                        break;
                }
                return true;
            }
        });

        m_DrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if(m_CurrentFragmentToSet != null) {
                    setActiveFragment(m_CurrentFragmentToSet, null);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new CryptocurrencyListFragment();
        transaction.replace(R.id.fragment_container, fragment, CryptocurrencyListFragment.class.getName());
        FragmentRepository.getInstance().addFragment(CryptocurrencyListFragment.class.getName(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        //test code for database
        WatchlistDao watchlistDao = ApplicationDatabase.getInstance(this).watchlistDao();
        if(watchlistDao.count() > 0)
            watchlistDao.deleteAll();
        watchlistDao.insert(new WatchlistEntity(0,0, "Bitcoin", "BTC"));
        watchlistDao.insert(new WatchlistEntity(1,1,"Ethereum", "ETH"));
        watchlistDao.insert(new WatchlistEntity(2,2, "Litecoin", "LTC"));
    }

    public <T extends Fragment> void setActiveFragment(Class<T> c, Bundle args) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //Fragment fragment = fragmentManager.findFragmentByTag(c.getName());
        Fragment fragment = FragmentRepository.getInstance().getFragment(c.getName());
        if(fragment == null) {
            try {
                fragment = (Fragment) c.newInstance();
                FragmentRepository.getInstance().addFragment(c.getName(), fragment);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        if(args != null) {
            fragment.setArguments(args);
        }
        transaction.replace(R.id.fragment_container, fragment, c.getName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public <T extends Fragment> void setActiveNewFragment(Class<T> c, Bundle args) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        try {
            fragment = (T) c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(args != null) {
            fragment.setArguments(args);
        }
        FragmentRepository.getInstance().addFragment(c.getName(), fragment);
        transaction.replace(R.id.fragment_container, fragment, c.getName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDrawerSelected(int itemId) {
        m_DrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public <T extends Fragment> void onLaunchFragment(Class<T> c, Bundle args, boolean createNewFragment) {
        if(createNewFragment) {
            setActiveNewFragment(c, args);
        }
        else {
            setActiveFragment(c, args);
        }
    }

    @Override
    public void onPopFragmentBackstack(String name, int flags) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(name == null) {
            fragmentManager.popBackStack();
        }
        else {
            fragmentManager.popBackStack(name, flags);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                //super.onSearchRequested();
                Intent intent = new Intent(this, SearchDetailsActivity.class);
                this.startActivity(intent);
                Log.d("SEARCH", "search item clicked");
                return true;
            case android.R.id.home:
                m_DrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
