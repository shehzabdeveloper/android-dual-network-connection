package com.example.envoybaseapp.child

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.envoybaseapp.R
import com.example.envoybaseapp.viewModel.ChildViewModel
import kotlinx.android.synthetic.main.activity_child.*

class ChildActivity : AppCompatActivity(), View.OnClickListener {

    private var childViewModel: ChildViewModel? = null
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)
        connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        childViewModel =
            ViewModelProviders.of(this).get(ChildViewModel::class.java)
        wifiBtn.setOnClickListener(this)
        internetBtn.setOnClickListener(this)
        childViewModel?.response?.observe(this, Observer {
            textView.text = it
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.wifiBtn -> {

                val networkRequest =
                    NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build()
                setNetworkType(networkRequest,true)

                textView.text = getString(R.string.loading_please_wait)
            }
            R.id.internetBtn -> {

                val networkRequest =
                    NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .build()
                setNetworkType(networkRequest,false)

                textView.text = getString(R.string.loading_please_wait)
            }
        }
    }

    private fun setNetworkType(networkRequest: NetworkRequest?, typeWifi: Boolean) {
        val connectivity = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("TAG: ", "Connected")
                connectivityManager.bindProcessToNetwork(network)
                super.onAvailable(network)
                if (typeWifi) {
                    childViewModel?.getWifiApiProperties()
                } else {
                    childViewModel?.getInternetApiProperties()
                }
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
