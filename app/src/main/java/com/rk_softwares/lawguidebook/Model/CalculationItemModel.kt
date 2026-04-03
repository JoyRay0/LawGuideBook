package com.rk_softwares.lawguidebook.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.OkHttpWrapper
import com.rk_softwares.lawguidebook.Helper.SecurityKey
import com.rk_softwares.lawguidebook.Helper.header
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.lang.Exception


data class CalculationData(
    val status : String = "",
    val message : String = "",

    @SerializedName("item")
    val items : List<Calculation> = emptyList()

)
data class Calculation(
    val id : Int = 0,
    val image : String = "",
    val title : String = "",
    val deviceID : String = ""
)

class CalculationItemModel {

    fun calculationAllItemServer(
        onSuccess : (List<Calculation>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getCalculationAllLink())
            .header()
            .execute(CalculationData::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.items)

                }else{

                    onFailed(true)

                }

            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end


}