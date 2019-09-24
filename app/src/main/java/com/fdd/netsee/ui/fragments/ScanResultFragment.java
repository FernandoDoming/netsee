package com.fdd.netsee.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fdd.netsee.R;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.ScanResult;
import com.fdd.netsee.ui.adapters.HostListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ScanResultFragment extends Fragment {
    private final static String ARG_SECTION_NUMBER = "section_number";
    private final static String ARG_SCAN_RESULT    = "scan";

    private final static int DEFAULT_SECTION_NUMER = 1;
    private final static int RAW_SECTION_NUMBER = 2;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ScanResultFragment newInstance(int sectionNumber, ScanResult result) {
        ScanResultFragment fragment = new ScanResultFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_SCAN_RESULT, result);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        final ScanResult result = (ScanResult) getArguments().getSerializable(ARG_SCAN_RESULT);
        View rootView = null;

        if (result == null) return null;

        switch (sectionNumber) {
            case DEFAULT_SECTION_NUMER:
                rootView = inflater.inflate(R.layout.scan_result_layout, container, false);
                fillDefaultSection(result, rootView);
                break;

            case RAW_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_raw_results_layout, container, false);
                TextView rawView = rootView.findViewById(R.id.raw_result);
                rawView.setText( result.getOutput() );
                break;
        }
        return rootView;
    }

    private void fillDefaultSection(ScanResult result, View root) {
        RecyclerView scanResultList = root.findViewById(R.id.scan_result_list);
        HostListAdapter scanResultAdapter = new HostListAdapter(result.getHosts());
        LinearLayoutManager layoutManager = new LinearLayoutManager( getContext() );
        scanResultList.setLayoutManager(layoutManager);
        scanResultList.setAdapter(scanResultAdapter);

        List<Host> upHosts = new ArrayList<>();
        List<Host> downHosts = new ArrayList<>();

        for (Host h : result.getHosts()) {
            if (h.getStatus().isUp()) {
                upHosts.add(h);
            }
            else {
                downHosts.add(h);
            }
        }

        TextView hostsUpSummary = root.findViewById(R.id.summary_hosts_up);
        hostsUpSummary.setText(
                String.format(
                        getString(R.string.summary_hosts),
                        upHosts.size(),
                        downHosts.size()
                )
        );

        TextView scanTimeSummary = root.findViewById(R.id.summary_scan_time);
        scanTimeSummary.setText(
                String.format(
                        getString(R.string.summary_time),
                        result.getElapsed()
                )
        );
    }
}
