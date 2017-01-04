package com.yuyu.module.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yuyu.module.R;
import com.yuyu.module.fragment.CallFragment;
import com.yuyu.module.fragment.HorizonFragment;
import com.yuyu.module.fragment.MainFragment;
import com.yuyu.module.fragment.MapFragment_;
import com.yuyu.module.fragment.SpinnerFragment;
import com.yuyu.module.fragment.TabFragment;
import com.yuyu.module.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends RxAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private Toast toast;
    private Context context;
    private int index;
    private ArrayList<Integer> items;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @BindView(R.id.nav_view)
    NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                toast.setText(getString(R.string.onBackPressed));
                toast.show();

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
        Fragment fragment = null;
        int iid = item.getItemId();
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
        }
        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }
        index = items.indexOf(iid);
        setTitle(item.getTitle());
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initialize() {
        items = new ArrayList<>();
        int size = nav_view.getMenu().size();
        for (int i = 0; i < size; i++) {
            items.add(nav_view.getMenu().getItem(i).getItemId());
        }
        setTitle(getString(R.string.nav_main));
        getFragmentManager().beginTransaction().replace(R.id.content_main, new MainFragment()).commit();
    }

    public Toast getToast() {
        return toast;
    }

}
