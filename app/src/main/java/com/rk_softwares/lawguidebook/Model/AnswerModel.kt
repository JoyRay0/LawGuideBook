package com.rk_softwares.lawguidebook.Model

import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.OkHttpWrapper
import com.rk_softwares.lawguidebook.Helper.header

data class Answer(
    @SerializedName("status")
    val status : String = "",

    @SerializedName("message")
    val message : String = "",

    @SerializedName("data")
    val answerData : AnswerData = AnswerData()
)

data class AnswerData(
    val question : String = "",
    val answer : String = ""
)

class AnswerModel {

    /*
    fun answer(
        answerData : AnswerData? = null,
        onSuccess : (AnswerData) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        if (answerData == null) return

        val client = OkHttpClient()

        val gson = Gson()

        val body : RequestBody = gson.toJson(answerData).toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request
            .Builder()
            .url(ApiLinks.getAnswerLink())
            .addHeader("API-KEY", SecurityKey.getSHA256())
            .addHeader("Device-ID", SecurityKey.getDeviceID())
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                onFailed(true)

            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful){

                    val data = response.body.string()

                    try {

                        val item = gson.fromJson(data, Answer::class.java)

                        if (item.status == "Success"){

                            onSuccess(item.answerData)
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

     */

    fun answer(
        answerData : AnswerData? = null,
        onSuccess : (AnswerData) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
        ){

        if (answerData == null) return

        OkHttpWrapper()
            .url(ApiLinks.getAnswerLink())
            .header()
            .post(answerData)
            .execute(Answer::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.answerData)

                }

            }, onFailed = { isFailed->

                onFailed(isFailed)

            }, onError = {})

    }


}