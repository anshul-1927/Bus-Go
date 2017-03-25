package dev.datvt.busfun.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dev.datvt.busfun.fragments.BusRouteFragment;
import dev.datvt.busfun.fragments.BusStopFragment;
import dev.datvt.busfun.fragments.BusTicketSalesFragment;

/**
 * Created by datvt on 7/31/2016.
 */
public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    final int TAB_COUNT = 3;
    private Fragment fr;

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                fr = new BusRouteFragment();
                break;
            case 1:
                fr = new BusStopFragment();
                break;
            case 2:
                fr = new BusTicketSalesFragment();
                break;
            default:
                break;
        }
        return fr;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
