package com.example.envoybaseapp.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


private const val WIFI_BASE_URL = "https://ghibliapi.herokuapp.com/"
private const val INTERNET_BASE_URL = "http://dummy.restapiexample.com/api/v1/"

private val retrofitWifi = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(WIFI_BASE_URL)
    .build()

private val retrofitInternet = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(INTERNET_BASE_URL)
    .build()

interface ApiService {
    @GET("films")
    fun getWifiData():
            Call<String>

    @GET("employees")
    fun getIntenetData():
            Call<String>
}

object Api {
    val retrofitWifiService: ApiService by lazy {
        retrofitWifi.create(ApiService::class.java)
    }
    val retrofitInternetService: ApiService by lazy {
        retrofitInternet.create(ApiService::class.java)
    }
}