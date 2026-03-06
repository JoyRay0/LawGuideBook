package com.rk_softwares.lawguidebook.Model

import com.google.gson.Gson
import com.rk_softwares.lawguidebook.Helper.ApiLinks
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
    val items : List<Calculation> = emptyList()

)
data class Calculation(
    val image : String = "",
    val title : String = "",
    val deviceID : String = ""
)

class CalculationItemModel {

    fun calculationServer(
        //answerData : AnswerData? = null,
        onSuccess : (List<Calculation>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        val client = OkHttpClient()

        val gson = Gson()

        //val body : RequestBody = gson.toJson(answerData.question).toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request
            .Builder()
            .url(ApiLinks.getCalculationLink())
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

                        val item = gson.fromJson(data, CalculationData::class.java)

                        if (item.status == "Success"){

                            onSuccess(item.items)
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