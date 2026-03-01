package com.rk_softwares.lawguidebook.Model

import android.util.Log
import com.google.gson.Gson
import com.rk_softwares.lawguidebook.Database.ChatDatabase
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.CacheHelper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.lang.Exception

data class ChatMessage(

    val id : Int = 0,
    val message : String = "",
    val user_message : String = "",
    val ai_message : String = "",
    val timestamp : String = "",
    val sender : String = "",
    val isUser : Boolean = false
)

class ChatModel(
    private val db: ChatDatabase,
    private val cache : CacheHelper
) {

    private val chaList = mutableListOf<ChatMessage>()

    fun dbUserInsert(msg : String){

        db.insert(message = msg, messageType = "user", isUser = true, timestamp = System.currentTimeMillis().toString())

        chaList.add(ChatMessage(user_message = msg, sender = "user"))
    }

    fun dbAiInsert(msg: String){

        db.insert(message = msg, messageType = "ai", isUser = false, timestamp = System.currentTimeMillis().toString())

        chaList.add(ChatMessage(ai_message = msg, sender = "ai"))
    }

    fun getAllMessage() : List<ChatMessage> {

        val dbList = db.getAll()

        chaList.clear()
        chaList.addAll(dbList)

        return chaList

    }

    fun dbDeleteSingleMessage(id: Int) : Boolean {

        return db.deleteOne(id)
    }

    fun dbDeleteAllMessage(){

        db.deleteAll()

    }

    fun cacheSet(key : String, value : String){

        cache.setCache(key, value)

    }

    fun cacheGet(key: String) : String?{

        val cache = cache.getCache(key)

        return cache
    }

    fun cacheDelete(key: String){

        cache.deleteCache(key)

    }

    fun sendMessageToServer(
        userMessage: ChatMessage? = null,
        onFailed : (Boolean) -> Unit = {},
        onSuccess : (ChatMessage) -> Unit = {},
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

                Log.d("failed", e.toString())

            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful){

                    val data = response.body.string()

                    try {

                        val item_message = gson.fromJson(data, ChatMessage::class.java)

                        //if (item_message.status == "success") onResultAvailable(true)

                        //Log.d("message", item_message.user_message)

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