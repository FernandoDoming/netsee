package com.fdd.netsee.ui.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fdd.netsee.models.ScanResult;
import com.fdd.netsee.ui.fragments.ScanResultFragment;

public class ScanResultPagerAdapter extends FragmentPagerAdapter {

    private final String SECTION_1_TITLE = "Default";
    private final String SECTION_2_TITLE = "Raw";

    private final String[] PAGES = {
            SECTION_1_TITLE,
            SECTION_2_TITLE
    };

    private ScanResult scanResult;

    public ScanResultPagerAdapter(FragmentManager fm, ScanResult scanResult) {
        super(fm);
        this.scanResult = scanResult;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ScanResultFragment.newInstance(position + 1, scanResult);
    }

    @Override
    public int getCount() {
        return PAGES.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return PAGES[position];
    }
}
