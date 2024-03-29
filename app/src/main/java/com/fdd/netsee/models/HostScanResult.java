package com.fdd.netsee.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HostScanResult extends ScanResult implements Serializable {

    private Host host;

    @Override
    public String getResult() {
        if (host.isUp()) {
            if (host.getServices().size() == 1) {
                return host.getServices().size() + " detected service open.";
            } else {
                return host.getServices().size() + " detected services open.";
            }
        } else {
            return "Host is offline";
        }
    }

    @Override
    public List<Host> getHosts() {
        List<Host> hosts = new ArrayList<>();
        hosts.add(host);
        return hosts;
    }

    @Override
    public String getTarget() {
        return host.getTarget();
    }

    public Host getHost() { return host; }

    public void setHost(Host host) {
        this.host = host;
    }
}
