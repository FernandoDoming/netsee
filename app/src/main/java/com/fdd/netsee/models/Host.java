package com.fdd.netsee.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Host implements Serializable {
    private String address;
    private List<Service> services;
    private List<String> hostnames = new ArrayList<>();
    private String os;
    private String mac;
    private String macVendor;
    private HostStatus status;
    private boolean isMe;

    public static List<String> HOST_DETAILS = Arrays.asList(
            "os", "mac", "mac_vendor", "hostname"
    );

    public Host() {
    }

    public Host(String address, HostStatus status) {
        this(address, null, status);
    }

    public Host(String address, String mac) {
        this(address, mac, new HostStatus(HostStatus.UNKNOWN));
    }

    public Host(String address, String mac, HostStatus status) {
        this(address, mac, new ArrayList<Service>(), status);
    }

    public Host(String address, String mac, List<Service> services, HostStatus status) {
        this.address  = address;
        this.mac      = mac;
        this.services = services;
        this.status   = status;
    }

    public String getTitle() {
        String title = "";

        if (isMe) title = "This device - ";
        if (hostnames.size() > 0)     title += hostnames.get(0);
        else if (address != null) title += address;
        else title += mac;

        return title;
    }

    public String getSubtitle() {
        if (macVendor != null) return macVendor;
        if (mac != null) return mac;

        return null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public HostStatus getStatus() { return status; }

    public void setStatus(HostStatus status) {
        this.status = status;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getTarget() {
        if (hostnames.size() > 0 && !hostnames.get(0).equals("")) {
            return hostnames.get(0);
        } else {
            return address;
        }
    }

    public boolean isUp() {
        return status.getState().equals(HostStatus.UP);
    }

    public String getMacVendor() {
        return macVendor;
    }

    public void setMacVendor(String macVendor) {
        this.macVendor = macVendor;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public List<String> getHostnames() {
        return hostnames;
    }

    public void setHostnames(List<String> hostnames) {
        this.hostnames = hostnames;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
        if (me) os = "Android";
    }
}
