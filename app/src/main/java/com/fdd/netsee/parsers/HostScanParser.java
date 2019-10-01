package com.fdd.netsee.parsers;

import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.HostScanResult;
import com.fdd.netsee.models.HostStatus;
import com.fdd.netsee.models.Service;
import com.fdd.netsee.models.ServiceStatus;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernando on 30/12/15.
 */
public class HostScanParser {

    private String ns = null;

    public HostScanResult parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        HostScanResult hostScanResult = new HostScanResult();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String name = parser.getName();
            switch (name) {
                case "host":
                    hostScanResult.setHost( readHost(parser) );
                    break;
                case "finished":
                    hostScanResult.setEndTime( Long.parseLong(parser.getAttributeValue(null, "time")) );
                    hostScanResult.setElapsed( Float.parseFloat(parser.getAttributeValue(null, "elapsed")) );
                    hostScanResult.setSummary( parser.getAttributeValue(null, "summary") );
                    break;
                case "nmaprun":
                    String startTime = parser.getAttributeValue(null, "start");
                    hostScanResult.setArgs( parser.getAttributeValue(null, "args") );
                    if (startTime != null) {
                        hostScanResult.setStartTime(Long.parseLong(startTime));
                    }
                    else {
                        hostScanResult.setStartTime(0);
                    }
                    break;
            }
        }

        return hostScanResult;
    }

    public Host readHost(XmlPullParser parser) throws XmlPullParserException, IOException {

        Host host = new Host();
        List<Service> services = new ArrayList<>();
        List<String> hostnames  = new ArrayList<>();

        int depth = parser.getDepth();
        while (!(parser.next() == XmlPullParser.END_TAG && parser.getDepth() == depth)) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String name = parser.getName();
            switch (name) {
                case "address":
                    String addrType = parser.getAttributeValue(null, "addrtype");
                    switch (addrType) {
                        case "ipv4":
                            host.setAddress(parser.getAttributeValue(null, "addr"));
                            break;
                        case "mac":
                            host.setMac(parser.getAttributeValue(null, "addr"));
                            host.setMacVendor(parser.getAttributeValue(null, "vendor"));
                            break;
                    }
                    break;
                case "ports":
                    services = readPorts(parser);
                    break;
                case "hostname":
                    hostnames.add(readHostname(parser));
                    break;
                //case "hosts":
                //    host.setUp(Integer.parseInt(parser.getAttributeValue(null, "up")) > 0);
                //    break;
                case "osmatch":
                    host.setOs(parser.getAttributeValue(null, "name"));
                    break;
                case "status":
                    host.setStatus(readHostStatus(parser));
                    break;
            }
        }
        host.setServices(services);
        host.setHostnames(hostnames);

        return host;
    }

    private List<Service> readPorts(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "ports");
        List<Service> services = new ArrayList<>();

        int depth = parser.getDepth();
        while (!(parser.next() == XmlPullParser.END_TAG && parser.getDepth() == depth)) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("port")) {
                services.add(readPort(parser));
            }
        }
        return services;
    }

    private Service readPort(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "port");

        ServiceStatus status = null;
        String protocol = null;
        String port = null;
        String service = null;
        String version = null;

        protocol = parser.getAttributeValue(null, "protocol");
        port     = parser.getAttributeValue(null, "portid");

        int depth = parser.getDepth();
        while (!(parser.next() == XmlPullParser.END_TAG && parser.getDepth() == depth)) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("state")) {
                status = readStatus(parser);
            } else if (name.equals("service")) {
                service = readService(parser);
                version = readVersion(parser);
            }
        }
        Service srvc = new Service(protocol, port, service, status);
        if(version != null) srvc.setVersion(version);
        return srvc;
    }

    private String readHostname(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "hostname");
        return parser.getAttributeValue(null, "name");
    }

    private String readService(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service");
        return parser.getAttributeValue(null, "name");
    }

    private String readVersion(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service");
        String product = parser.getAttributeValue(null, "product");
        String version = parser.getAttributeValue(null, "version");
        String extra = parser.getAttributeValue(null, "extrainfo");
        String v = "";
        if (product != null) v = v + product;
        if (version != null) v = v + version;
        if (extra != null)   v = v + extra;
        if (v.equals(""))    v = "Unknown";
        return v;
    }

    private ServiceStatus readStatus(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "state");
        String state = parser.getAttributeValue(null, "state");
        String reason = parser.getAttributeValue(null, "reason");
        return new ServiceStatus(state, reason);
    }

    private HostStatus readHostStatus(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "status");
        String status = parser.getAttributeValue(null, "state");
        String reason = parser.getAttributeValue(null, "reason");
        //parser.require(XmlPullParser.END_TAG, ns, "status");
        return new HostStatus(status, reason);
    }
}