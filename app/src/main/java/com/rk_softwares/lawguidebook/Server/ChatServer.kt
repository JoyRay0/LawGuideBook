package com.rk_softwares.lawguidebook.Server

import com.google.gson.Gson
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Model.ChatModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.lang.Exception

object ChatServer {

    fun chatData(
        userMessage : ChatModel? = null,
        onSuccess : (ChatModel) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        if (userMessage == null) return

        val client = OkHttpClient()

        val gson = Gson()

        val body : RequestBody = gson.toJson(userMessage).toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request
            .Builder()
            .url(ApiLinks.getChatLink())
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

                        val item_message = gson.fromJson(data, ChatModel::class.java)

                        onSuccess(item_message)
                        onFailed(false)

                    }catch (e : Exception){

                        e.printStackTrace()
                        onFailed(true)

                    }

                }else{

                    onFailed(true)

                }


            }
        })

    }

}