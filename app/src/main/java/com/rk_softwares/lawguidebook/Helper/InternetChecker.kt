package com.rk_softwares.lawguidebook.Helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*

interface InternetStatus{
    fun isInternet(internet : Boolean)
}

class InternetChecker(
    private val view : InternetStatus
) {

    @Composable
    fun LiveInternetStatus(context: Context){

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        DisposableEffect(Unit) {

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    view.isInternet(true)
                }

                override fun onLost(network: Network) {
                    view.isInternet(false)
                }
            }

            cm.registerDefaultNetworkCallback(callback)

            onDispose {
                cm.unregisterNetworkCallback(callback)
            }
        }

    }


}