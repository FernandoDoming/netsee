package com.fdd.netsee.models;

import java.io.Serializable;
import java.util.List;

public abstract class ScanResult implements Serializable {

    private String name;
    private String target;
    private String args;

    private long startTime;
    private long endTime;
    private float elapsed;

    private String scanStatus;
    private String output;
    private String summary;

    /* Abstract methods */

    public abstract List<Host> getHosts();
    public abstract String getResult();

    /* Public methods */

    /* Getters / setters */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public float getElapsed() {
        return elapsed;
    }

    public void setElapsed(float elapsed) {
        this.elapsed = elapsed;
    }

    public String getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(String scanStatus) {
        this.scanStatus = scanStatus;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        if (name != null) return name;
        if (target != null) return target;
        if (args != null) return args.split(" ")[args.split(" ").length - 1];

        return "Unnamed scan";
    }
}
