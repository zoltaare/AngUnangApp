package com.example.zoltaare.angunangapp;

/*
 * Copyright 2013 WhiteByte (Nick Russler, Ahmet Yueksektepe).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.app.Activity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.TextView;

        import org.apache.http.conn.util.InetAddressUtils;

        import java.io.IOException;
        import java.net.InetAddress;
        import java.net.NetworkInterface;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;

public class MainActivity extends Activity {
    TextView textView1;
    WifiApManager wifiApManager;
    private webserver w;
//    private
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView1 = (TextView) findViewById(R.id.textView1);
        wifiApManager = new WifiApManager(this);
        scan();

    }

    //CREATE
    public void click_create(View v){
        //start wifi hotspot
        wifiApManager.setWifiApEnabled(null, true);
        //instantiate server
        w = new webserver();
        try {
            w.start();
            textView1.append("Server init. Listening on : " + getIPAddress(true) + ":1234");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void scan() {
        wifiApManager.getClientList(false, new FinishScanListener() {
            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {
                for (ClientScanResult clientScanResult : clients) {
                    textView1.append("####################\n");
                    textView1.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                    textView1.append("Device: " + clientScanResult.getDevice() + "\n");
                    textView1.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                    textView1.append("isReachable: " + clientScanResult.isReachable() + "\n");
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Get Clients");
        menu.add(0, 1, 0, "Open AP");
        menu.add(0, 2, 0, "Close AP");
        menu.add(0, 3, 0, "Show My IP");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                scan();
                break;
            case 1:
                if(wifiApManager.getWifiApState() == WIFI_AP_STATE.valueOf("WIFI_AP_STATE_DISABLED")){
                    wifiApManager.setWifiApEnabled(null, true);
                    textView1.setText("WifiApState: " + wifiApManager.getWifiApState() + "\n\n");
                }
                break;
            case 2:
                if(wifiApManager.getWifiApState() == WIFI_AP_STATE.valueOf("WIFI_AP_STATE_ENABLED")){
                    wifiApManager.setWifiApEnabled(null, false);
                    textView1.setText("WifiApState: " + wifiApManager.getWifiApState() + "\n\n");
                }
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    //UNNECESSARY :)
    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.d("GET_IP", "" + ex);
        }
        return "";
    }

//    DEFAULTS
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(wifiApManager.getWifiApState() == WIFI_AP_STATE.valueOf("WIFI_AP_STATE_ENABLED")){
            wifiApManager.setWifiApEnabled(null, false);
        }
        w.stop();
        super.onDestroy();
    }

}