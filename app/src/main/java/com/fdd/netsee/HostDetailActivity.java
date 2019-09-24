package com.fdd.netsee;

import android.os.Bundle;

import com.fdd.netsee.constants.Extras;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.ui.adapters.HostPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;
import android.view.View;

import com.fdd.netsee.R;

public class HostDetailActivity extends AppCompatActivity {

    private Host host;
    private ViewPager viewPager;
    private HostPagerAdapter hostPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_detail);

        host = (Host) getIntent().getSerializableExtra(Extras.HOST_EXTRA);

        hostPagerAdapter = new HostPagerAdapter(getSupportFragmentManager(), host);

        viewPager = findViewById(R.id.host_viewpager);
        viewPager.setAdapter(hostPagerAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(host.getTitle());
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
