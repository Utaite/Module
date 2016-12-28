package com.yuyu.module.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yuyu.module.R;
import com.yuyu.module.custom.Constant;
import com.yuyu.module.fragment.MainFragment;
import com.yuyu.module.fragment.TabFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private Toast toast;
    private Context context;

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
        } else if (iid == R.id.nav_3) {
        } else if (iid == R.id.nav_4) {
        } else if (iid == R.id.nav_5) {
        } else if (iid == R.id.nav_6) {
        }
        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initialize() {
        nav_view.getMenu().getItem(0).setChecked(true);
        getFragmentManager().beginTransaction().replace(R.id.content_main, new MainFragment()).commit();
    }

}
