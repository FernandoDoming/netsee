package com.fdd.netsee.helpers;

import android.content.Context;

import com.fdd.netsee.R;
import com.fdd.netsee.models.HostScan;
import com.fdd.netsee.models.NetworkScan;
import com.fdd.netsee.models.ScanIntensity;

public class ScanHelper {

    public static String intensityKeyFromValue (Context context, String value) {

        String key = null;
        switch (value) {
            case NetworkScan.SCAN_TYPE_HOST_DISCOVERY:
                key = ScanIntensity.HOST_DISCOVERY;
                break;

            case NetworkScan.SCAN_TYPE_SERVICE_DISCOVERY:
            case HostScan.SCAN_TYPE_SERVICE_SCAN:
                key = ScanIntensity.SERVICE_SCAN;
                break;

            case HostScan.SCAN_TYPE_HOST_REGULAR:
                key = ScanIntensity.REGULAR_SCAN;
                break;

            case HostScan.SCAN_TYPE_HOST_INTENSE:
                key = ScanIntensity.INTENSE_SCAN;
                break;

            case HostScan.SCAN_TYPE_HOST_FAST:
                key = ScanIntensity.FAST_SCAN;
                break;

            case HostScan.SCAN_TYPE_HOST_INTENSE_ALL_TCP:
                key = ScanIntensity.INTENSE_SCAN_ALL_TCP_PORTS;
                break;
        }

        return key;
    }

    public static int getDrawableIcon(String os) {
        if (os == null) return R.drawable.ic_host;

        if (os.startsWith("Apple")) {
            return R.drawable.apple;

        } else if (os.startsWith("Microsoft")) {
            return R.drawable.microsoft;

        } else if (os.startsWith("Linux")) {
            return R.drawable.tux;

        } else if (os.startsWith("Android")) {
            return R.drawable.android;

        } else {
            return R.drawable.ic_host;
        }
    }
}
