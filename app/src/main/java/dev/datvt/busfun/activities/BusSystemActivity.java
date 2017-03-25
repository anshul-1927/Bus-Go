package dev.datvt.busfun.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import dev.datvt.busfun.R;
import dev.datvt.busfun.adapters.MyViewPagerAdapter;
import dev.datvt.busfun.utils.Contants;

/**
 * Created by datvt on 8/12/2016.
 */
public class BusSystemActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyViewPagerAdapter viewPagerAdapter;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_system_bus);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.bus_route)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.bus_stop)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.ticket_sale)));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.custom_bus_station));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.custom_bus_stop));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.custom_bus_ticket));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            item.setChecked(true);
        } else if (id == R.id.nav_bus_system) {
            item.setChecked(true);
            Intent intent_station = new Intent(this, BusSystemActivity.class);
            startActivityForResult(intent_station, Contants.REQUEST_BUS_STATION);
        } else if (id == R.id.nav_search_bus_station_nearest_me) {

        } else if (id == R.id.nav_search_location) {
            item.setChecked(true);
            Intent intent_location = new Intent(this, FindLocation.class);
            startActivityForResult(intent_location, Contants.REQUEST_LOCATION);
        } else if (id == R.id.nav_find_road) {
            item.setChecked(true);
            Intent intent_route = new Intent(this, FindRouteActivity.class);
            startActivityForResult(intent_route, Contants.REQUEST_ROUTE);
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_vote) {
            openGooglePlay();
        } else if (id == R.id.nav_read_new) {
            finish();
            startActivity(new Intent(this, PaperActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ứng dụng có hiệu quả cao");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phản hồi về Bus Funny");
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ"));
    }

    private void openGooglePlay() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
