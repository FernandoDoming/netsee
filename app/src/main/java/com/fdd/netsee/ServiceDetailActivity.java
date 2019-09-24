package com.fdd.netsee;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.fdd.netsee.constants.Extras;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.Service;

public class ServiceDetailActivity extends AppCompatActivity {

    private Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        service = (Service) getIntent().getSerializableExtra(Extras.SERVICE_EXTRA);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle( service.getTitle() );
        }

        TextView productView = findViewById(R.id.product);
        TextView versionView = findViewById(R.id.version);
        TextView cpeView = findViewById(R.id.cpe);

        if (service.getProduct() != null) productView.setText(service.getProduct());
        if (service.getVersion() != null) versionView.setText(service.getVersion());
        if (service.getCpe() != null)     cpeView.setText(service.getCpe());
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
