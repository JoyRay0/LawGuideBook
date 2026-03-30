package com.rk_softwares.lawguidebook.Helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface InternetStatus{
    fun isInternet(internet : Boolean)
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

    }

    fun onStart(){

        cm.registerDefaultNetworkCallback(callback)
        scope.launch { view.isInternet(isConnected()) }


    }

    fun onStop(){

        cm.unregisterNetworkCallback(callback)
        scope.cancel()

    }

    private fun isConnected(): Boolean {
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

}