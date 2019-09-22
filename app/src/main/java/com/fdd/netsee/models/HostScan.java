package com.fdd.netsee.models;


public class HostScan extends Scan {
    public static final String SCAN_TYPE_HOST_REGULAR = "Regular scan";
    public static final String SCAN_TYPE_HOST_INTENSE = "Intense scan";
    public static final String SCAN_TYPE_SERVICE_SCAN = "Service scan";
    public static final String SCAN_TYPE_HOST_FAST = "Fast scan";
    public static final String SCAN_TYPE_HOST_INTENSE_ALL_TCP = "Intense scan - All TCP ports";

    public static final String[] SCAN_TYPES = new String[] {
        SCAN_TYPE_HOST_REGULAR,
        SCAN_TYPE_HOST_INTENSE,
        SCAN_TYPE_HOST_FAST,
        SCAN_TYPE_SERVICE_SCAN,
        SCAN_TYPE_HOST_INTENSE_ALL_TCP
    };

    public HostScan() {
        this.type = ScanType.HOST_SCAN;
    }

}
