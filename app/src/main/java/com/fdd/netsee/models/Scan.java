package com.fdd.netsee.models;

import android.content.Context;
import android.os.AsyncTask;

import com.fdd.netsee.async.NmapExecutor;

public class Scan {
    protected String target;
    protected ScanType type;
    protected String intensity;
    protected ScanResult scanResult;

    private AsyncTask scanTask;

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public void run(Context context, String binary) {
         scanTask = new NmapExecutor(context, binary).execute(this);
    }

    public void stop() {
        scanTask.cancel(true);
    }

    public ScanType getType() {
        return type;
    }

    public void setType(ScanType type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }
}
