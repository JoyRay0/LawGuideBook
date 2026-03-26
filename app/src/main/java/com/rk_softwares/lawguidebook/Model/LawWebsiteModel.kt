package com.rk_softwares.lawguidebook.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.SecurityKey
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.lang.Exception

data class Websites(

    @SerializedName("status")
    val status : String = "",

    @SerializedName("message")
    val message : String = "",

    @SerializedName("data")
    val data : List<WebsiteData> = emptyList()

)

data class WebsiteData(

    @SerializedName("id")
    val id : Int = 0,

    @SerializedName("title")
    val title : String = "",

    @SerializedName("data")
    val websiteLink : String = "",

    @SerializedName("device_id")
    val deviceID : String = ""

)

class LawWebsiteModel {

    fun allWebsiteLinks(
        //qData : Data? = null,
        onSuccess : (List<WebsiteData>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        //if (qData == null) return

        val client = OkHttpClient()

        val gson = Gson()

        //val body : RequestBody = gson.toJson(qData.category).toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request
            .Builder()
            .url(ApiLinks.getWebsitesLink())
            .addHeader("API-KEY", SecurityKey.getSHA256())
            .addHeader("Device-ID", SecurityKey.getDeviceID())
            //.post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                onFailed(true)

            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful){

                    val data = response.body.string()

                    try {

                        val data = gson.fromJson(data, Websites::class.java)

                        if (data.status == "Success"){

                            onSuccess(data.data)
                            onFailed(false)

                        }else{

                            onFailed(true)
                        }


                    }catch (e : Exception){

                        e.printStackTrace()
                        onFailed(true)

                    }

                }else{

                    onFailed(true)

                }


            }
        })

    }//fun end

}