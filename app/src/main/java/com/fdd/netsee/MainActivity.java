package com.fdd.netsee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fdd.netsee.async.SimpleHttpTask;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.Scan;
import com.fdd.netsee.ui.adapters.HostListAdapter;
import com.fdd.netsee.ui.dialogs.HostScanBottomDialog;
import com.fdd.netsee.ui.dialogs.NetworkScanBottomDialog;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private String NMAP_BINARY_FILE;
    public String NMAP_DOWNLOAD_URL;
    public String NMAP_VERSION_URL;

    public ProgressDialog sharedProgressDialog;
    public ProgressBar scanProgressBar;
    private Snackbar scanSnackbar;

    private View scanRunningNoticeContainer;
    private RecyclerView scanResultList;
    private View scanResultContainer;
    private HostListAdapter scanResultAdapter;

    private final Context context = this;
    private Scan runningScan = null;
    private int currentEabi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wrapper);

        /*
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        */

        NMAP_DOWNLOAD_URL = getResources().getString(R.string.default_update_url);
        NMAP_VERSION_URL  = getResources().getString(R.string.default_version_url);
        NMAP_BINARY_FILE = getFilesDir().getParent() + "/bin/nmap";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("netsee");
        }

        initViews();

        try {
            installNmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_options:
                break;

            case R.id.actions_about:

                break;
        }
        return true;
    }

    private void initViews() {
        sharedProgressDialog = new ProgressDialog(this);
        sharedProgressDialog.setMessage(getString(R.string.dlg_progress_title_download));
        sharedProgressDialog.setIndeterminate(true);
        sharedProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        sharedProgressDialog.setCancelable(false);

        scanProgressBar = findViewById(R.id.scan_progress_bar);
        scanRunningNoticeContainer = findViewById(R.id.scan_running_container);
        findViewById(R.id.stop_scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRunningScan();
            }
        });

        scanResultContainer = findViewById(R.id.scan_results_container);
        scanResultList = findViewById(R.id.scan_result_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        scanResultAdapter = new HostListAdapter(new ArrayList<Host>());
        scanResultList.setLayoutManager(layoutManager);
        scanResultList.setAdapter(scanResultAdapter);

        SpeedDialView speedDialView = findViewById(R.id.newScanButton);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_net_scan, R.drawable.ic_lan)
                        .setLabel(R.string.net_scan)
                        .setFabBackgroundColor(getResources().getColor(R.color.colorPrimary))
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_host_scan, R.drawable.ic_host)
                        .setLabel(R.string.host_scan)
                        .setFabBackgroundColor(getResources().getColor(R.color.colorPrimary))
                        .create()
        );

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.fab_host_scan:
                        newHostScan();
                        return false; // true to keep the Speed Dial open

                    case R.id.fab_net_scan:
                        newNetScan();
                        return false;

                    default:
                        return false;
                }
            }
        });
    }

    public void newHostScan() {
        HostScanBottomDialog hostScanDialog = new HostScanBottomDialog();
        hostScanDialog.show(
                getSupportFragmentManager(),
                "new_hostscan_dialog_fragment"
        );
        onInitScan();
    }

    public void newNetScan() {
        NetworkScanBottomDialog networkScanDialog = new NetworkScanBottomDialog();
        networkScanDialog.show(
                getSupportFragmentManager(),
                "new_netscan_dialog_fragment"
        );

        onInitScan();
    }
    
    private void onInitScan() {
        scanResultContainer.setVisibility(View.GONE);
        scanRunningNoticeContainer.setVisibility(View.GONE);
        findViewById(R.id.no_scans_container).setVisibility(View.VISIBLE);
    }

    public void onScanInProgress() {
        View root = findViewById(R.id.container);

        findViewById(R.id.no_scans_container).setVisibility(View.GONE);
        scanRunningNoticeContainer.setVisibility(View.VISIBLE);
        scanProgressBar.setVisibility(View.VISIBLE);

        /*scanSnackbar = Snackbar.make(
                root, getString(R.string.scan_running), Snackbar.LENGTH_INDEFINITE
        ).setAction("STOP", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRunningScan();
            }
        });
        scanSnackbar.show();*/
    }

    public void onScanCompleted(Scan scan) {
        findViewById(R.id.no_scans_container).setVisibility(View.GONE);
        scanRunningNoticeContainer.setVisibility(View.GONE);
        scanProgressBar.setVisibility(View.GONE);
        runningScan = null;
        //scanSnackbar.dismiss();

        if (scan == null) return;

        if (scan.getScanResult() != null) {
            /* Scan success */
            scanResultContainer.setVisibility(View.VISIBLE);
            List<Host> hosts = scan.getScanResult().getHosts();
            scanResultAdapter.updateData(hosts);

            List<Host> upHosts = new ArrayList<>();
            List<Host> downHosts = new ArrayList<>();

            for (Host h : hosts) {
                if (h.getStatus().isUp()) {
                    upHosts.add(h);
                }
                else {
                    downHosts.add(h);
                }
            }

            TextView hostsUpSummary = scanResultContainer.findViewById(R.id.summary_hosts_up);
            hostsUpSummary.setText(
                    String.format(
                            getString(R.string.summary_hosts),
                            upHosts.size(),
                            downHosts.size()
                    )
            );

            TextView scanTimeSummary = scanResultContainer.findViewById(R.id.summary_scan_time);
            scanTimeSummary.setText(
                    String.format(
                            getString(R.string.summary_time),
                            scan.getScanResult().getElapsed()
                    )
            );
        }
        else {
            /* Scan error */
            String errorStr = getResources().getString(R.string.scan_error);
            Snackbar.make(scanResultContainer, errorStr, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public void runScan(Scan scan) {
        scan.run(this, NMAP_BINARY_FILE);
        runningScan = scan;
        onScanInProgress();
    }

    public void stopRunningScan() {
        if (runningScan != null) {
            runningScan.stop();
            onScanCompleted(null);
        }
    }

    /* Helper functions */
    // TODO: Move them to a helper class

    private void installNmap() throws Exception {
        if (!isBinaryHere(NMAP_BINARY_FILE)) {
            askToDownload();
        }
    }

    private void askToDownload() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    downloadAll();
                }
                else {
                    finish();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogTheme)
        );
        builder.setMessage(getString(R.string.dlg_ask2download))
                .setPositiveButton(getString(R.string.dlg_ask2download_yes), dialogClickListener)
                .setNegativeButton(getString(R.string.dlg_ask2download_no), dialogClickListener)
                .setCancelable(false)
                .show();
    }

    private void downloadAll () {
        currentEabi = 0;
        final SimpleHttpTask versionTask = new SimpleHttpTask(this);
        String versionurl = NMAP_VERSION_URL + "/nmap-latest.txt";
        versionTask.execute(versionurl);
    }


    private boolean isBinaryHere(String binary) {
        File binaryFile = new File(binary);
        return binaryFile.canExecute();
    }

    @SuppressWarnings("deprecation")
    public String doNextEabi() {
        switch (currentEabi++) {
            case 0:
                return Build.CPU_ABI;
            case 1:
                return Build.CPU_ABI2;
        }
        return null;
    }
}
