package com.example.envoybaseapp.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.envoybaseapp.R
import com.example.envoybaseapp.child.ChildActivity
import com.example.envoybaseapp.utils.WifiReceiver
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var wifiManager: WifiManager
    private lateinit var connectivityManager: ConnectivityManager
    private var receiverWifi: WifiReceiver? = null
    private var myPermissionsAccessFineLocation: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(applicationContext, "Turning wifi on...", Toast.LENGTH_SHORT).show()
            wifiManager.isWifiEnabled = true
        }
        scanBtn.setOnClickListener(this)
        makeApiBtn.setOnClickListener(this)
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                connectToWifi((view as TextView).text.toString())
            }
    }


    override fun onPostResume() {
        super.onPostResume()
        receiverWifi = WifiReceiver(wifiManager, listView)
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(receiverWifi, intentFilter)
        getWifi()
    }

    private fun getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    myPermissionsAccessFineLocation
                )
            } else {
                wifiManager.startScan()
            }
        } else {
            Toast.makeText(this, "scanning", Toast.LENGTH_SHORT).show()
            wifiManager.startScan()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiverWifi)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            myPermissionsAccessFineLocation -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                    wifiManager.startScan()
                } else {
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.scanBtn -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        myPermissionsAccessFineLocation
                    )
                } else {
                    wifiManager.startScan()
                }

            }
            R.id.makeApiBtn -> {
                if (isDeviceOnline(this)) {
                    startActivity(Intent(this, ChildActivity::class.java))
                } else {
                    Toast.makeText(this, "Check internet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun connectToWifi(wifiSSID: String) {

        val alertDialog =
            AlertDialog.Builder(this@MainActivity)
        alertDialog.setTitle("PASSWORD")
        alertDialog.setMessage("Enter Password")
        val input = EditText(this@MainActivity)
        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        alertDialog.setView(input)

        alertDialog.setPositiveButton("Ok") { _, _ ->
            finallyConnect(input.text.toString(), wifiSSID)
        }

        alertDialog.setNegativeButton("Cancel") { _, _ ->
        }
        alertDialog.show()
    }


    private fun finallyConnect(networkPass: String, networkSSID: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            androidQHigherVersion(networkSSID, networkPass)
        } else {
            androidQLowerVersion(networkSSID, networkPass)
        }
    }

    /*
     *
     * METHOD TO CONNECT DEVICE FROM VERSION M TO VERSION P.
     */

    private fun androidQLowerVersion(networkSSID: String, networkPass: String) {
        val netId: Int
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = String.format("\"%s\"", networkSSID)

        if (networkPass.isEmpty()) {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        } else {
            wifiConfig.preSharedKey = String.format("\"%s\"", networkPass)
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)

            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)

            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.GCMP_256)

            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.GCMP_256)
        }
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)

        netId = wifiManager.addNetwork(wifiConfig)
        var connected = false
        if (netId != -1) {

            Log.d("TAG:", "selected id : $netId")
            wifiManager.disconnect()

            connected = wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
        }

        if (connected) {
            Toast.makeText(this, "Connected Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show()
        }
    }

    /*
     *
     *  METHOD TO CONNECT DEVICE FROM VERSION Q TO HIGHER.
     *  AS PER DOCUMENTATION, CURRENTLY WE ARE ONLY ABLE TO CONNECT WIFI BUT NOT ABLE TO GET INTERNET THROUGH WIFI.
     *  GOOGLE NEED TO FIX THIS ISSUE.
     */
    private fun androidQHigherVersion(networkSSID: String, networkPass: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
                .setSsid(networkSSID)
                .setWpa2Passphrase(networkPass)
                .build()

            val networkRequest =
                NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(wifiNetworkSpecifier)
                    .build()

            val connectivity = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d("TAG: ", "Connected")
//                    connectivityManager.bindProcessToNetwork(network)
                    super.onAvailable(network)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    Log.d("TAG: ", "Not able to connect")
                }

                override fun onLost(network: Network) {
                    Log.d("TAG: ", "Connection lost")
                }
            }
            connectivityManager.requestNetwork(networkRequest, connectivity)
        }
    }

    /*
     *
     * METHOD TO CHECK DEIVCE IS ABLE TO CONNECT INTERNET
     */
    private fun isDeviceOnline(context: Context?): Boolean {
        var isConnected = false
        context?.let {
            val connectivityManager = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                isConnected = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        true
                    }
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        true
                    }
                    else -> {
                        false
                    }
                }
            } else {
                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                return nwInfo.isConnected
            }
        }
        return isConnected
    }

}
