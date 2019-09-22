package com.fdd.netsee.models;

import java.io.Serializable;

public class HostStatus implements Serializable {
    private String state;
    private String reason;

    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String UNKNOWN = "unknown";

    public HostStatus(String state) {
        this.state = state;
    }

    public HostStatus(String state, String reason) {
        this.state = state;
        this.reason = reason;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isUp() {
        return this.state.equals(HostStatus.UP);
    }

    @Override
    public String toString() {
        return state;
    }
}
