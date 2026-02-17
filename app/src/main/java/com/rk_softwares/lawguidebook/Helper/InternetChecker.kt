package com.rk_softwares.lawguidebook.Helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

object InternetChecker {

    @Composable
    fun liveInternetStatus(context: Context) : Boolean {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        var isConnected by remember { mutableStateOf(false) }

        DisposableEffect(Unit) {

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isConnected = true
                }

                override fun onLost(network: Network) {
                    isConnected = false
                }
            }

            cm.registerDefaultNetworkCallback(callback)

            onDispose {
                cm.unregisterNetworkCallback(callback)
            }
        }

        return isConnected

        //Toast.makeText(context,if (isConnected) "On" else "Off", Toast.LENGTH_SHORT).show()

    }


}