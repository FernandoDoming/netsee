package com.fdd.netsee.parsers;

import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.NetworkScan;
import com.fdd.netsee.models.NetworkScanResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernando on 30/12/15.
 */
public class NetworkScanParser {

    private String ns = null;

    public NetworkScanResult parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Host> hosts = new ArrayList<>();

        NetworkScanResult networkScanResult = new NetworkScanResult();
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "host":
                    Host host = new HostScanParser().readHost(parser);
                    if (host.isUp()) {
                        hosts.add(host);
                    }
                    break;
                case "finished":
                    networkScanResult.setEndTime(Long.parseLong(parser.getAttributeValue(null, "time")));
                    networkScanResult.setElapsed(Float.parseFloat(parser.getAttributeValue(null, "elapsed")));
                    networkScanResult.setSummary(parser.getAttributeValue(null, "summary"));
                    break;
                case "nmaprun":
                    networkScanResult.setStartTime(Long.parseLong(parser.getAttributeValue(null, "start")));
                    break;
            }
        }
        networkScanResult.setHosts(hosts);
        return networkScanResult;
    }
}