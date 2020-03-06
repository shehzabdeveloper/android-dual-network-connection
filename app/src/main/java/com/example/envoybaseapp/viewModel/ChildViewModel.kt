package com.example.envoybaseapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.envoybaseapp.network.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChildViewModel : ViewModel() {

    var response: MutableLiveData<String> = MutableLiveData()
        private set


    fun getWifiApiProperties() {
        Api.retrofitWifiService.getWifiData().enqueue(
            object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    response.postValue("Failure: " + t.message)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    this@ChildViewModel.response.postValue(response.body())
                }
            }
        )
    }
    fun getInternetApiProperties() {
        Api.retrofitInternetService.getIntenetData().enqueue(
            object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    response.postValue("Failure: " + t.message)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    this@ChildViewModel.response.postValue(response.body())
                }
            }
        )
    }
}
