package com.example.envoybaseapp.utils

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.widget.ArrayAdapter
import android.widget.ListView


class WifiReceiver(wifiManager: WifiManager, wifiList: ListView) : BroadcastReceiver() {
    var wifiManager: WifiManager = wifiManager
    var wifiDeviceList: ListView = wifiList


    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == action) {
            val wifiList: List<ScanResult> = wifiManager.scanResults
            val deviceList: ArrayList<String> = ArrayList()
            for (scanResult in wifiList) {
                if (scanResult.SSID != null && scanResult.SSID.isNotEmpty() && scanResult.SSID.startsWith(
                        "ENVOY",
                        false
                    )
                ) {
                    deviceList.add(scanResult.SSID/*+"-"+scanResult.BSSID*/)
                }
            }
            val arrayAdapter: ArrayAdapter<*>? = context?.let {
                ArrayAdapter(
                    it,
                    R.layout.simple_list_item_1,
                    deviceList.toArray()
                )
            }
            wifiDeviceList.setAdapter(arrayAdapter)
        }
    }
}