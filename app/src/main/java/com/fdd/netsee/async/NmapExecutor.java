package com.fdd.netsee.async;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;

import com.fdd.netsee.MainActivity;
import com.fdd.netsee.R;
import com.fdd.netsee.helpers.NetworkHelper;
import com.fdd.netsee.models.Host;
import com.fdd.netsee.models.HostStatus;
import com.fdd.netsee.models.NetworkScan;
import com.fdd.netsee.models.NetworkScanResult;
import com.fdd.netsee.models.Nmap;
import com.fdd.netsee.models.Scan;
import com.fdd.netsee.models.ScanResult;
import com.fdd.netsee.models.ScanType;
import com.fdd.netsee.parsers.HostScanParser;
import com.fdd.netsee.parsers.NetworkScanParser;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Coded by fernando on 29/12/15.
 */
public class NmapExecutor extends AsyncTask<Scan, Integer, Scan> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    private Nmap nmap;

    public NmapExecutor(Context context, String binary) {
        this.context = context;
        this.nmap = new Nmap(context, binary);
    }

    @Override
    protected void onPreExecute() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        Log.d(this.getClass().getName(), "Acquiring wakelock");
        mWakeLock.acquire();
    }

    @Override
    protected Scan doInBackground(Scan... params) {
        Scan scan = params[0];
        String output = "";
        ScanResult scanResult = null;
        Log.i(this.getClass().getName(), "Starting scan for " + scan.getTarget());
        Log.i(this.getClass().getName(), "Scan intensity: "   + scan.getIntensity());

        try {
            Method method = nmap.getClass().getMethod(scan.getIntensity(), String.class);
            output = (String) method.invoke(nmap, scan.getTarget());

            // Parse the result into POJOs
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(output));

            if (scan.getType() == ScanType.HOST_SCAN) {
                // Host scan
                scanResult = new HostScanParser().parse(parser);
                Host h = scanResult.getHosts().get(0);

                if (h.getMac() == null) {
                    String mac = macForHost(h.getAddress());
                    if (mac != null) {
                        h.setMac(mac);
                    }
                }
            }
            else if (scan.getType() == ScanType.NET_SCAN) {
                // Network scan
                scanResult = new NetworkScanParser().parse(parser);

                try {
                    NetworkScanResult ns = (NetworkScanResult) scanResult;
                    for (Host h : ns.getHosts()) {
                        if (h.getMac() == null) continue;

                        String mac = macForHost(h.getAddress());
                        if (mac != null) {
                            h.setMac(mac);
                        }
                    }
                } catch (ClassCastException e) {
                    Log.e(this.getClass().getName(),
                            "Could not add ARP hosts because scanResult is not a NetworkScan");
                }

            } else {
                // Neither
                Log.e(this.getClass().getName(),
                        "Scan is neither HOST nor NETWORK. Scan will fail.");
            }

            if (scanResult != null) {
                for (Host host : scanResult.getHosts()) enrichHost(host);
                scanResult.setTarget( scan.getTarget() );

            } else {
                Log.w(this.getClass().getName(), "Scan result was null");
            }

        } catch (NoSuchMethodException | SecurityException e) {
            Log.e(this.getClass().getName(), scan.getIntensity() + " is not supported");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(this.getClass().getName(), "Invocation for " + scan.getIntensity()
                    + " in " + nmap + " failed");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(this.getClass().getName(), "Illegal access made: " + scan.getIntensity()
                    + " in " + nmap );
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (scanResult != null) {
            scanResult.setOutput(output);
            scan.setScanResult(scanResult);
        }
        return scan;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(final Scan scan) {

        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.onScanCompleted(scan);
        }

        Log.i(this.getClass().getName(), "Scan finished for " + scan.getTarget());
        mWakeLock.release();
    }

    private List<Host> readArpTable() {
        List<Host> hosts = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    // Basic sanity check
                    String mac = splitted[3];
                    String ip = splitted[0];

                    if (mac.matches("..:..:..:..:..:..") && !mac.equals("00:00:00:00:00:00")) {
                        HostStatus status = new HostStatus(HostStatus.UNKNOWN, "ARP table entry");
                        hosts.add(new Host(ip, mac, status));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hosts;
    }

    private String macForHost(String ip)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    // Basic sanity check
                    String mac = splitted[3];
                    String hostIP = splitted[0];

                    if (!hostIP.equals(ip)) continue;

                    if (mac.matches("..:..:..:..:..:..") && !mac.equals("00:00:00:00:00:00")) {
                        return mac;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void enrichHost(Host host) {
        if (host == null) return;

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        byte[] bytes = BigInteger.valueOf(ipAddress).toByteArray();
        String iface = NetworkHelper.getIfaceByIp(bytes);

        String address = host.getAddress();
        String mac = host.getMac();

        if (address != null && address.equals(NetworkHelper.getIPAddress())) {
            host.setMe(true);
        }
        else if ( mac != null && mac.equals(NetworkHelper.getMACAddress(iface)) ) {
            host.setMe(true);
        }
    }
}