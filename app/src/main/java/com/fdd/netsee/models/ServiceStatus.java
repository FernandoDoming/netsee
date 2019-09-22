package com.fdd.netsee.models;

import java.io.Serializable;

public class ServiceStatus implements Serializable {
    private String state;
    private String reason;

    public ServiceStatus(String state, String reason) {
        this.state = state;
        this.reason = reason;
    }

    public String getState() {
        return state;
    }

    public String getReason() {
        return reason;
    }
}
