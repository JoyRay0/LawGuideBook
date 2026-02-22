package com.rk_softwares.lawguidebook.Helper

import android.content.Context
import androidx.core.content.edit

class CacheHelper(
    val context: Context,
    val preference : String
) {

    fun setCache(key : String, value : String){

        val sp = context.getSharedPreferences(preference, Context.MODE_PRIVATE)
        sp.edit { putString(key, value) }

    }

    fun getCache(key: String) : String? {

        val sp = context.getSharedPreferences(preference, Context.MODE_PRIVATE)
        val data = sp.getString(key, "")

        return data
    }

    fun deleteCache(key: String){

        val sp = context.getSharedPreferences(preference, Context.MODE_PRIVATE)
        sp.edit().remove(key).apply()

    }

}