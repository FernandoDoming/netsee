package com.fdd.netsee.ui.adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fdd.netsee.models.Host;
import com.fdd.netsee.ui.fragments.HostDetailFragment;

/**
 * Created by fernando on 24/02/16.
 */
public class HostPagerAdapter extends FragmentPagerAdapter {

    private final String SECTION_1_TITLE = "General";
    private final String SECTION_2_TITLE = "Network";
    private final int    NUM_PAGES       = 2;

    private Host host;

    public HostPagerAdapter(FragmentManager fm, Host host) {
        super(fm);
        this.host = host;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return HostDetailFragment.newInstance(position + 1, host);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return SECTION_1_TITLE;
            case 1:
                return SECTION_2_TITLE;
        }
        return null;
    }
}
