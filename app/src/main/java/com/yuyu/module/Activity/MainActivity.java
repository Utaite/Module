package com.yuyu.module.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yuyu.module.R;
import com.yuyu.module.fragment.CallFragment;
import com.yuyu.module.fragment.CameraFragment;
import com.yuyu.module.fragment.HorizonFragment;
import com.yuyu.module.fragment.MainFragment;
import com.yuyu.module.fragment.MapFragment_;
import com.yuyu.module.fragment.SpinnerFragment;
import com.yuyu.module.fragment.TabFragment;
import com.yuyu.module.chain.ChainedArrayList;
import com.yuyu.module.chain.ChainedToast;
import com.yuyu.module.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends RxAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private ChainedToast toast;
    private ArrayList<Integer> items;

    private int index;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @BindView(R.id.nav_view)
    NavigationView nav_view;
    @BindView(R.id.bottom_tab_bar)
    BottomBar bottom_tab_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toast = new ChainedToast(this).makeTextTo(this, "", Toast.LENGTH_SHORT);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nav_view.getMenu().getItem(index).setChecked(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        nav_view.getMenu().getItem(0).setChecked(true);

    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);

        } else {
            if (Constant.CURRENT_TIME + Constant.BACK_TIME < System.currentTimeMillis()) {
                Constant.CURRENT_TIME = System.currentTimeMillis();
                toast.setTextShow(getString(R.string.onBackPressed));

            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int iid = item.getItemId();
        if (iid == R.id.menu_develop) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        getFragmentManager().beginTransaction()
                .replace(R.id.content_main, getFragment(item.getItemId()))
                .commit();
        setTitle(item.getTitle());
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    public Fragment getFragment(int iid) {
        index = items.indexOf(iid);
        Fragment fragment = null;
        if (iid == R.id.nav_main) {
            fragment = new MainFragment();
        } else if (iid == R.id.nav_tab) {
            fragment = new TabFragment();
        } else if (iid == R.id.nav_horizon) {
            fragment = new HorizonFragment();
        } else if (iid == R.id.nav_map) {
            fragment = new MapFragment_();
        } else if (iid == R.id.nav_call) {
            fragment = new CallFragment();
        } else if (iid == R.id.nav_spinner) {
            fragment = new SpinnerFragment();
        } else if (iid == R.id.nav_camera) {
            fragment = new CameraFragment();
        }
        return fragment;
    }

    public void setTab(int tabId) {
        for (int i = 0; i < bottom_tab_bar.getTabCount(); i++) {
            bottom_tab_bar.getTabAtPosition(i).setPadding(0, 30, 0, 0);
            TextView title = (TextView) bottom_tab_bar.getTabAtPosition(i).findViewById(R.id.bb_bottom_bar_title);
            title.setVisibility(View.GONE);
        }

        bottom_tab_bar.getTabWithId(tabId).setPadding(0, 5, 0, 0);
        TextView title = (TextView) bottom_tab_bar.getTabWithId(tabId).findViewById(R.id.bb_bottom_bar_title);
        title.setVisibility(View.VISIBLE);

        if (tabId == R.id.bottom_tab_camera) {
            toast.setTextShow(getString(R.string.bottom_tab_camera));
        } else if (tabId == R.id.bottom_tab_gallery) {
            toast.setTextShow(getString(R.string.bottom_tab_gallery));
        } else if (tabId == R.id.bottom_tab_manage) {
            toast.setTextShow(getString(R.string.bottom_tab_manage));
        } else if (tabId == R.id.bottom_tab_send) {
            toast.setTextShow(getString(R.string.bottom_tab_send));
        }
    }

    public void initialize() {
        for (int i = 0; i < bottom_tab_bar.getTabCount(); i++) {
            bottom_tab_bar.getTabAtPosition(i).setScaleX(1.25f);
            bottom_tab_bar.getTabAtPosition(i).setScaleY(1.25f);
        }
        bottom_tab_bar.setOnTabSelectListener(this::setTab);

        items = (ArrayList<Integer>) new ChainedArrayList()
                .addMenu(nav_view.getMenu(), 0, nav_view.getMenu().size());
        setTitle(getString(R.string.nav_main));
        getFragmentManager().beginTransaction()
                .replace(R.id.content_main, new MainFragment())
                .commit();
    }

    public ChainedToast getToast() {
        return toast;
    }

}
