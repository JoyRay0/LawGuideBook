package com.rk_softwares.lawguidebook.Helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

interface InternetStatus{
    fun isInternet(internet : Boolean = false)
}

class InternetChecker(
    private val view : InternetStatus,
    private val context: Context
) {

    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val scope = CoroutineScope(Dispatchers.Main)

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            scope.launch { view.isInternet(true) }
        }

        override fun onLost(network: Network) {
            scope.launch { view.isInternet(false) }
        }

        override fun onUnavailable() {
            scope.launch { view.isInternet(false) }
        }

    }

    fun onStart(){

        cm.registerDefaultNetworkCallback(callback)

        checkCurrentStatus()
    }

    fun onStop(){

        cm.unregisterNetworkCallback(callback)
        scope.cancel()

    }

    private fun checkCurrentStatus() {
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)
        val isConnected = capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        CoroutineScope(Dispatchers.Main).launch { view.isInternet(isConnected) }
    }

}