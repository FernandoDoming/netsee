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
import com.fdd.netsee.models.HostScan;
import com.fdd.netsee.models.NetworkScan;
import com.fdd.netsee.models.Scan;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class HostScanBottomDialog extends BottomSheetDialogFragment {

    private EditText addrEditText;
    private AutoCompleteTextView scanIntensityEditText;

    protected HostScanBottomDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        dialog = this;
        View view = inflater.inflate(R.layout.host_scan_bottom_sheet, container,
                false);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        HostScan.SCAN_TYPES
                );

        addrEditText = view.findViewById(R.id.host_address);
        scanIntensityEditText = view.findViewById(R.id.scan_type_dropdown);
        scanIntensityEditText.setText(HostScan.SCAN_TYPES[0]);
        scanIntensityEditText.setAdapter(adapter);

        Button submit = view.findViewById(R.id.scan_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();

                String hostAddr = addrEditText.getText().toString();
                String intensity = scanIntensityEditText.getText().toString();

                Scan scan = new HostScan();
                scan.setTarget( hostAddr );
                scan.setIntensity(
                        ScanHelper.intensityKeyFromValue(getContext(), intensity)
                );

                try {
                    activity.runScan(scan);
                } catch (NullPointerException e) {
                    Log.e("HostScanBottom", "Invalid activity pointer");
                }

                dialog.dismiss();
            }
        });

        return view;
    }
}
