package com.fdd.netsee.models;

public class NetworkScan extends Scan {

    public static final String SCAN_TYPE_HOST_DISCOVERY = "Host discovery";
    public static final String SCAN_TYPE_SERVICE_DISCOVERY = "Service discovery";

    public static final String[] SCAN_TYPES = new String[] {
            SCAN_TYPE_HOST_DISCOVERY,
            SCAN_TYPE_SERVICE_DISCOVERY
    };

    public NetworkScan() {
        this.type = ScanType.NET_SCAN;
    }

}
