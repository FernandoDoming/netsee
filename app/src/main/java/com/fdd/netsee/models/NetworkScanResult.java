package com.fdd.netsee.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NetworkScanResult extends ScanResult implements Serializable {
    private List<Host> hosts;

    public NetworkScanResult() {}

    public NetworkScanResult(List<Host> hosts) {
        this.hosts = hosts;
    }

    @Override
    public String getResult() {
        List<Host> upHosts = getUpHosts();
        if (upHosts.size() == 1) {
            return upHosts.size() + " detected host up.";
        } else {
            return upHosts.size() + " detected hosts up.";
        }
    }

    public List<Host> getHosts() { return hosts; }

    public void addHost(Host host) {
        boolean shouldAdd = true;
        for (Host h : hosts) {
            if (host.getAddress().equals(h.getAddress())) {
                shouldAdd = false;
                break;
            }
        }

        if (shouldAdd) {
            hosts.add(host);
        }
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public List<Host> getUpHosts() {
        List<Host> allHosts = getHosts();
        List<Host> upHosts = new ArrayList<>();
        for (Host host : allHosts) {
            if (host.getStatus() != null &&
                    host.getStatus().getState() != null &&
                    host.getStatus().getState().equals(HostStatus.UP)) {
                upHosts.add(host);
            }
        }
        return upHosts;
    }
}
