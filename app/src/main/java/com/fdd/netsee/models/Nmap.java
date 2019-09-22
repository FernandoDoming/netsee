package com.fdd.netsee.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fdd.netsee.R;
import com.fdd.netsee.helpers.NetworkHelper;

import java.io.IOException;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;


/**
 * Created by fernando on 28/12/15.
 */
public class Nmap {

    private Context context;
    private String binary;

    private String target;

    public Nmap(Context context, String binary) {
        this.context = context;
        this.binary  = binary;
    }

    public String version() throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.version_opts), "" );
    }

    public String regularScan(String target) throws IOException, InterruptedException {
        return execute( context.getString(R.string.regular_scan_opts), target );
    }

    public String serviceScan(String target) throws IOException, InterruptedException {
        return execute( context.getString(R.string.service_scan_opts), target );
    }

    public String intenseScan(String target) throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.intense_scan_opts), target );
    }

    public String fastScan(String target) throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.fast_scan_opts), target );
    }

    public String intenseScanAllTcpPorts(String target) throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.intense_scan_all_tcp_opts), target );
    }

    public String hostDiscovery(String target) throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.host_discovery_opts), target );
    }

    public String osScan(String target) throws IOException, InterruptedException {
        return execute(context.getResources().getString(R.string.os_scan_opts), target, true);
    }

    public String osServiceScan(String target) throws IOException, InterruptedException {
        return execute(context.getResources().getString(R.string.os_service_opts), target, true);
    }

    public String hostExtDiscovery(String target) throws IOException, InterruptedException {
        return execute(context.getResources().getString(R.string.host_discovery_opts), target, true);
    }

    public String hostServiceDiscovery(String target) throws IOException, InterruptedException {
        return execute(context.getResources().getString(R.string.host_service_discovery_opts), target, true);
    }

    public String hostOsDiscovery(String target) throws IOException, InterruptedException {
        return execute(context.getResources().getString(R.string.host_os_discovery_opts), target, true);
    }

    public String hostOsServiceDiscovery(String target) throws IOException, InterruptedException {
        return execute(context.getResources().getString(R.string.host_os_service_discovery_opts), target, true);
    }

    private String execute(String options, String target) throws IOException, InterruptedException {
        return execute(options, target, false);
    }

    private String execute(String options, String target, boolean root) throws IOException, InterruptedException {
        String xml = context.getResources().getString(R.string.xml_opt);
        String cmd = binary + " " + options + " " + xml + " " + target;
        // If the target is a private IP add the gateway as a dns server
        // so rDNS IP resolution can be done
        if ( NetworkHelper.isPrivateAddress(target.split("/")[0]) ) {
            cmd += " --dns-servers " + NetworkHelper.getDefaultGw(context);
        }
        else {
            cmd += " --dns-servers 8.8.8.8";
        }

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE
        );
        final boolean alwaysRoot = sharedPref.getBoolean(
                context.getString(R.string.run_always_as_root), false
        );

        Log.i(this.getClass().getName(), "Running nmap cmd: " + cmd);
        
        List<String> outlines;
        if (root || alwaysRoot) {
            outlines = Shell.SU.run(cmd);
        } else {
            outlines = Shell.SH.run(cmd);
        }

        String output = "";
        for (String line : outlines) {
            output += "\n" + line;
        }

        return output.trim();
    }
}