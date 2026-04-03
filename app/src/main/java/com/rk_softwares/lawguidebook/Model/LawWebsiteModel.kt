package com.rk_softwares.lawguidebook.Model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.OkHttpWrapper
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

    @SerializedName("website_link")
    val websiteLink : String = "",

    @SerializedName("device_id")
    val deviceID : String = ""

)

class LawWebsiteModel {

    fun allWebsiteLinks(
        onSuccess : (List<WebsiteData>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getWebsitesLink())
            .execute(Websites::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.data)

                }else{

                    onFailed(true)

                }

            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

}