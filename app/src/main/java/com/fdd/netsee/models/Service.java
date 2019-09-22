package com.fdd.netsee.models;

import java.io.Serializable;

public class Service implements Serializable {
    private String protocol;
    private String port;
    private String service;
    private String version;
    private String cpe;
    private ServiceStatus status;

    public Service(String protocol, String port, String service, ServiceStatus status) {
        this.protocol = protocol;
        this.port = port;
        this.service = service;
        this.status = status;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPort() {
        return port;
    }

    public String getService() {
        return service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ServiceStatus getStatus() { return status; }

    public String getCpe() {
        return cpe;
    }

    public void setCpe(String cpe) {
        this.cpe = cpe;
    }
}
