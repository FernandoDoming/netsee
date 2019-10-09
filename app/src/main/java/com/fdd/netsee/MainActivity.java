package com.fdd.netsee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fdd.netsee.async.SimpleHttpTask;
import com.fdd.netsee.constants.Extras;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.Scan;
import com.fdd.netsee.models.ScanResult;
import com.fdd.netsee.models.Service;
import com.fdd.netsee.parsers.NetworkScanParser;
import com.fdd.netsee.ui.adapters.SavedScansListAdapter;
import com.fdd.netsee.ui.dialogs.HostScanBottomDialog;
import com.fdd.netsee.ui.dialogs.NetworkScanBottomDialog;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private String NMAP_BINARY_FILE;
    public String NMAP_DOWNLOAD_URL;
    public String NMAP_VERSION_URL;

    public ProgressDialog sharedProgressDialog;
    public ProgressBar scanProgressBar;
    private Snackbar scanSnackbar;

    private View noScansNoticeContainer;

    private final Context context = this;
    private Scan runningScan = null;
    private int currentEabi;

    private boolean multiSelect = false;
    private List<ScanResult> selectedItems = new ArrayList<>();

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_actions_bar_context, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.discard_button:
                    for (ScanResult r : selectedItems) {
                        File outputFile = new File(r.filepath);
                        outputFile.delete();
                        // Reload list
                        loadSavedScansIntoUI();
                    }
                    break;
            }

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
        }
    };

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

    private void findViews() {
        scanProgressBar = findViewById(R.id.scan_progress_bar);
        noScansNoticeContainer = findViewById(R.id.no_scans_container);
    }

    private void initViews() {
        findViews();

        sharedProgressDialog = new ProgressDialog(this);
        sharedProgressDialog.setMessage(getString(R.string.dlg_progress_title_download));
        sharedProgressDialog.setIndeterminate(true);
        sharedProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        sharedProgressDialog.setCancelable(false);

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

        loadSavedScansIntoUI();
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

    }

    public void onScanInProgress() {
        View root = findViewById(R.id.container);

        noScansNoticeContainer.setVisibility(View.GONE);
        scanProgressBar.setVisibility(View.VISIBLE);

        scanSnackbar = Snackbar.make(
                root, getString(R.string.scan_running), Snackbar.LENGTH_INDEFINITE
        ).setAction("STOP", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRunningScan();
            }
        });
        scanSnackbar.show();
    }

    public void onScanCompleted(Scan scan) {
        noScansNoticeContainer.setVisibility(View.GONE);
        scanProgressBar.setVisibility(View.GONE);
        runningScan = null;
        scanSnackbar.dismiss();

        if (scan == null) return;

        if (scan.getScanResult() != null) {
            /* Scan success */
            saveScan(scan.getScanResult());
            loadSavedScansIntoUI();

            Intent i = new Intent(this, ScanResultActivity.class);
            i.putExtra(Extras.SCAN_RESULT_EXTRA, scan.getScanResult());
            startActivity(i);
        }
        else {
            /* Scan error */
            String errorStr = getResources().getString(R.string.scan_error);
            Snackbar.make(
                findViewById(R.id.container),
                errorStr, Snackbar.LENGTH_LONG
            ).show();
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

    private void loadSavedScansIntoUI() {
        Map<String, List<ScanResult>> saveds = loadScans();
        if (saveds.isEmpty()) {
            noScansNoticeContainer.setVisibility(View.VISIBLE);
        }
        else {
            noScansNoticeContainer.setVisibility(View.GONE);
            populateSavedScanList(saveds);
        }
    }

    private Map<String, List<ScanResult>> loadScans() {
        File dir = getDir("results", Context.MODE_PRIVATE);
        File[] files = dir.listFiles();
        Map<String, List<ScanResult>> savedScans = new HashMap<>();
        if (files == null) return savedScans;

        for (File f : files) {
            XmlPullParser parser = Xml.newPullParser();
            try {
                String content = new Scanner(f).useDelimiter("\\Z").next();
                parser.setInput(new StringReader(content));
                ScanResult result = new NetworkScanParser().parse(parser);
                result.setOutput(content);
                result.filepath = f.getPath();

                String target = result.getTitle();
                if (savedScans.get(target) == null) {
                    savedScans.put(target, new ArrayList<ScanResult>());
                }
                savedScans.get(target).add(result);

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
        }

        return savedScans;
    }

    private void populateSavedScanList( Map<String, List<ScanResult>> results) {
        LinearLayout container = findViewById(R.id.saved_scans_container);
        container.removeAllViews();

        for (Map.Entry<String, List<ScanResult>> entry : results.entrySet()) {
            View child = getLayoutInflater().inflate(R.layout.saved_scans_list_by_target, null);
            TextView title = child.findViewById(R.id.saved_scans_title);
            TextView count = child.findViewById(R.id.saved_scans_count);
            title.setText(entry.getKey());
            count.setText(
                getResources().getString(R.string.scan_count, entry.getValue().size())
            );

            LinearLayout scanResultList = child.findViewById(R.id.saved_scans_list);
            for (ScanResult result : entry.getValue()) {
                View item = getLayoutInflater().inflate(R.layout.saved_scan_list_item, null);
                bindScanResultToView(item, result);
                scanResultList.addView(item);
            }

            container.addView(child);
        }
    }

    private void saveScan(ScanResult result) {
        File resultsdir = getDir("results", Context.MODE_PRIVATE);
        if (!resultsdir.exists()) resultsdir.mkdirs();

        long timstamp = System.currentTimeMillis() / 1000L;
        String filename = resultsdir.getPath() + "/" + result.getTarget() + "_" + timstamp + ".xml";

        Log.i(getClass().getName(), "Saving scan result to " + filename);

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(filename));
            stream.write(result.getOutput().getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bindScanResultToView(View view, final ScanResult result) {
        TextView subtitle   = view.findViewById(R.id.scan_result_date);
        ImageView typeIcon  = view.findViewById(R.id.summary_scan_icon);
        TextView onlineView = view.findViewById(R.id.summary_scan_online);
        View clickable      = view.findViewById(R.id.scan_result_clickable);

        Date scandate = new Date(result.getEndTime() * 1000);
        String formatted = new SimpleDateFormat("dd-mm-yyyy hh:mm").format(scandate);
        subtitle.setText( getString(R.string.scanned_at, formatted) );

        if (result.getHosts().size() == 1) {
            Host host = result.getHosts().get(0);
            typeIcon.setImageDrawable(
                    getDrawable(R.drawable.ic_ethernet)
            );

            List<Service> services = host.getServices();
            int openServices = 0, closedServices = 0;

            for (Service s : services) {
                if (s.getStatus().isOpen()) {
                    openServices++;
                }
                else {
                    closedServices++;
                }
            }

            onlineView.setText( Integer.toString(openServices) );
        }
        else {
            int upHosts = 0, downHosts = 0;

            for (Host h : result.getHosts()) {
                if (h.getStatus().isUp()) {
                    upHosts++;
                }
                else {
                    downHosts++;
                }
            }

            onlineView.setText( Integer.toString(upHosts) );
        }

        clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (multiSelect) {
                    selectItem(result, view);
                }
                else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ScanResultActivity.class);
                    intent.putExtra(Extras.SCAN_RESULT_EXTRA, result);
                    context.startActivity(intent);
                }
            }
        });

        clickable.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Context context = view.getContext();
                AppCompatActivity activity = (AppCompatActivity) context;
                activity.startSupportActionMode(actionModeCallbacks);
                selectItem(result, view);

                return true;
            }
        });
    }

    private void selectItem(ScanResult item, View view) {
        if (multiSelect) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
                highlightView(view, false);
            } else {
                selectedItems.add(item);
                highlightView(view, true);
            }
        }
    }

    private void highlightView(View view, boolean highlighted) {
        if (highlighted) {
            view.setBackgroundColor(
                    view.getContext().getResources().getColor(R.color.highlight_color)
            );
        }
        else {
            view.setBackgroundColor(
                    view.getContext().getResources().getColor(R.color.white)
            );
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
