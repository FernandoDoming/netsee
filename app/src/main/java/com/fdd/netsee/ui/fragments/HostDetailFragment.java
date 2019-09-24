package com.fdd.netsee.ui.fragments;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.fdd.netsee.R;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.Service;
import com.fdd.netsee.ui.adapters.ServicesAdapter;
import com.google.android.material.chip.Chip;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class HostDetailFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private final static String ARG_SECTION_NUMBER = "section_number";
    private final static String ARG_SCAN_RESULT    = "scan";

    private final static int GENERAL_SECTION_NUMBER = 1;
    private final static int NETWORK_MAP_SECTION_NUMBER = 2;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HostDetailFragment newInstance(int sectionNumber, Host host) {
        HostDetailFragment fragment = new HostDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_SCAN_RESULT, host);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        final Host host = (Host) getArguments().getSerializable(ARG_SCAN_RESULT);
        View rootView = null;

        if (host == null) return null;

        switch (sectionNumber) {
            case GENERAL_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.host_general_results_layout, container, false);
                fillGeneralSection(host, rootView);
                fillServicesList(host, rootView);
                break;

            case NETWORK_MAP_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_network_map_layout, container, false);
                break;
        }
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void fillGeneralSection(Host host, View root)
    {
        Resources res = root.getContext().getResources();

        TextView addressView   = root.findViewById(R.id.host_address);
        TextView reasonView    = root.findViewById(R.id.reason);
        TextView macView       = root.findViewById(R.id.host_mac);
        TextView macVendorView = root.findViewById(R.id.host_mac_vendor);
        TextView osView        = root.findViewById(R.id.host_os);
        TextView hostnameView  = root.findViewById(R.id.host_hostname);
        Chip onlineChip        = root.findViewById(R.id.online_chip);

        addressView.setText( host.getAddress() );
        reasonView.setText( humanizeReason(host.getStatus().getReason()) );

        if (host.getMac() != null) {
            macView.setText(host.getMac());
        }
        if (host.getMacVendor() != null) {
            macVendorView.setText(host.getMacVendor());
        }
        if (host.getOs() != null) {
            osView.setText(host.getOs());
        }
        if (host.getHostname() != null) {
            hostnameView.setText(host.getHostname());
        }

        if ( host.isUp() ) {
            onlineChip.setText("online");
            int color = res.getColor(R.color.nice_green);
            onlineChip.setTextColor(color);
            /* Alpha values range from 00 to FF */
            onlineChip.setChipBackgroundColor( res.getColorStateList(R.color.nice_green_25) );
        }
        else {
            onlineChip.setText("offline");
            int color = res.getColor(R.color.hot_pink);
            onlineChip.setTextColor(color);
            onlineChip.setChipBackgroundColor( res.getColorStateList(R.color.hot_pink_25) );
        }
    }

    private void fillServicesList(Host host, View root)
    {
        ListView servicesList  = root.findViewById(R.id.services_list);
        final List<Service> services = host.getServices();

        servicesList.setEmptyView( root.findViewById(R.id.empty_services) );
        ServicesAdapter customAdapter = new ServicesAdapter(
                this.getContext(), R.layout.service_list_row, services
        );
        servicesList.setAdapter(customAdapter);
        ViewCompat.setNestedScrollingEnabled(servicesList, true);
    }

    private String humanizeReason(String reason) {
        String text = reason;
        switch (reason) {
            case "syn-ack":
                text = getString(R.string.reason_syn_ack);
                break;

            case "conn-refused":
                text = getString(R.string.reason_conn_refused);
                break;
        }

        return text;
    }
}
