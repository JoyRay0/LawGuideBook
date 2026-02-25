package com.rk_softwares.lawguidebook.Helper

import android.app.Activity
import android.content.Intent

object IntentHelper {

    fun dataIntent(activity: Activity, cls : Class<*> ,key : String, value : String){

        val intent = Intent(activity, cls)
        intent.putExtra(key, value)
        activity.startActivity(intent)

    }

    fun normalIntent(activity: Activity, cls: Class<*>){

        activity.startActivity(Intent(activity, cls))

    }

}