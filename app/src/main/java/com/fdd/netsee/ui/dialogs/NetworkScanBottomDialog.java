package com.fdd.netsee.ui.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.fdd.netsee.MainActivity;
import com.fdd.netsee.R;
import com.fdd.netsee.helpers.NetworkHelper;
import com.fdd.netsee.helpers.ScanHelper;
import com.fdd.netsee.models.NetworkScan;
import com.fdd.netsee.models.Scan;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NetworkScanBottomDialog extends BottomSheetDialogFragment {

    private EditText netAddrEditText;
    private EditText netmaskEditText;
    private AutoCompleteTextView scanIntensityEditText;

    protected NetworkScanBottomDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        dialog = this;
        View view = inflater.inflate(R.layout.network_scan_bottom_sheet, container,
                false);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        NetworkScan.SCAN_TYPES
                );

        netAddrEditText = view.findViewById(R.id.network_address);
        netmaskEditText = view.findViewById(R.id.netmask);
        scanIntensityEditText = view.findViewById(R.id.scan_type_dropdown);
        scanIntensityEditText.setText(NetworkScan.SCAN_TYPES[0]);
        scanIntensityEditText.setAdapter(adapter);

        /* Default values */
        String lanAddr = NetworkHelper.getNetworkAddress();
        String[] parts = lanAddr.split("/");
        netAddrEditText.setText(parts[0]);
        netmaskEditText.setText(parts[1]);

        Button submit = view.findViewById(R.id.scan_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();

                String net_addr = netAddrEditText.getText().toString();
                String netmask  = netmaskEditText.getText().toString();
                String intensity = scanIntensityEditText.getText().toString();

                Scan scan = new NetworkScan();
                scan.setTarget( net_addr + "/" + netmask );
                scan.setIntensity(
                        ScanHelper.intensityKeyFromValue(getContext(), intensity)
                );

                try {
                    activity.runScan(scan);
                } catch (NullPointerException e) {
                    Log.e("NetworkScanBottom", "Invalid activity pointer");
                }

                dialog.dismiss();
            }
        });

        return view;
    }
}
