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

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    private var scope: CoroutineScope? = null
    private var isRegistered = false

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            scope?.launch { view.isInternet(true) }
        }

        override fun onLost(network: Network) {
            scope?.launch { view.isInternet(false) }
        }
    }

    fun onStart() {
        if (isRegistered) return // double register ঠেকাবে

        scope = CoroutineScope(Dispatchers.Main)
        cm.registerDefaultNetworkCallback(callback)
        isRegistered = true
        scope?.launch { view.isInternet(isConnected()) }
    }

    fun onStop() {
        if (!isRegistered) return // double unregister ঠেকাবে

        try {
            cm.unregisterNetworkCallback(callback)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            isRegistered = false
            scope?.cancel()
            scope = null
        }
    }

    private fun isConnected(): Boolean {
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

}