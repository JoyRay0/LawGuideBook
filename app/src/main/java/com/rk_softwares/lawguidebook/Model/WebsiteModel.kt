package com.rk_softwares.lawguidebook.Model

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.OkHttpWrapper

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

    @SerializedName("website_link", alternate = (arrayOf("url")))
    val websiteLink : String = "",

    @SerializedName("device_id")
    val deviceID : String = ""

)

class WebsiteModel {

    fun allLawWebsiteLinks(
        onSuccess : (List<WebsiteData>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getLawWebsitesLink())
            .execute(Websites::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.data)

                }else{

                    onFailed(true)

                }

            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

    fun allGovWebsiteLinks(
        onSuccess : (List<WebsiteData>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getGovtWebsites())
            .execute(Websites::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.data)

                }else{

                    onFailed(true)

                }

            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

}