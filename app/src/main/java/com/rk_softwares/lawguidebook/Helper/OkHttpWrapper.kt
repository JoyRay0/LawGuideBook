package com.rk_softwares.lawguidebook.Helper

import android.util.Log
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

class OkHttpWrapper {

    private val client = OkHttpClient()
    private val gson = Gson()
    private var httpUrl : String = ""
    private var body : RequestBody? = null
    private var method : String = "GET"

    internal val headers = mutableMapOf<String, String>()

    fun get() : OkHttpWrapper{

        method = "GET"

        return this
    }

    fun post(post : Any) : OkHttpWrapper{

        method = "POST"

        body = gson.toJson(post).toRequestBody("application/json; charset=utf-8".toMediaType())

        return this
    }

    fun url(url : String) : OkHttpWrapper{

         httpUrl = url

        return this
    }

    fun <T> execute(dataClass : Class<T>, onSuccess : (T) -> Unit, onFailed : (Boolean) -> Unit, onError : (String) -> Unit = {}) : OkHttpWrapper{

        val requestBuilder = Request.Builder().url(httpUrl)

        headers.forEach { it ->

            requestBuilder.addHeader(it.key, it.value)

        }

        val request = if (method == "POST"){

            if (body == null) throw IllegalArgumentException("POST request require body")

            requestBuilder.post(body!!).build()

        }else{

            requestBuilder.get().build()

        }

        client.newCall(request).enqueue(object : Callback{

            override fun onFailure(call: Call, e: IOException) {

                onFailed(true)
                onError(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful){

                    val responseBody = response.body?.string() ?: ""

                    try {

                        val parsed = gson.fromJson(responseBody, dataClass)

                        onSuccess(parsed)

                    }catch (e : Exception){

                        onFailed(true)
                        onError(e.message ?: "JSON parse error")
                    }

                }else{

                    onError("HTTP error code : {${response.code}}")

                    onFailed(true)

                }

            }

        })

        return this

    }

}//class end

fun OkHttpWrapper.header() : OkHttpWrapper{

    headers["API-KEY"] = SecurityKey.getSHA256()
    headers["Device-ID"] = SecurityKey.getDeviceID()

    return this
}
