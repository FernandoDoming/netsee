package com.fdd.netsee;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.fdd.netsee.constants.Extras;
import com.fdd.netsee.models.ScanResult;
import com.fdd.netsee.ui.adapters.HostPagerAdapter;
import com.fdd.netsee.ui.adapters.ScanResultPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class ScanResultActivity extends AppCompatActivity {

    private ScanResult scanResult;
    private ViewPager viewPager;
    private ScanResultPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        scanResult = (ScanResult) getIntent().getSerializableExtra(Extras.SCAN_RESULT_EXTRA);

        pagerAdapter = new ScanResultPagerAdapter(getSupportFragmentManager(), scanResult);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_format_list_bulleted_square);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_code_tags);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(scanResult.getTitle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
