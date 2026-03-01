package com.rk_softwares.lawguidebook.Presenter

import android.util.Log
import com.rk_softwares.lawguidebook.Database.ChatDatabase
import com.rk_softwares.lawguidebook.Helper.CacheHelper
import com.rk_softwares.lawguidebook.Model.ChatMessage
import com.rk_softwares.lawguidebook.Model.ChatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


interface ChatView{
    fun messages(messages : List<ChatMessage>)
    fun messageStatus(status : String)
    fun deleteStatus(isDeleted : Boolean, message : String)
    fun cacheStatus(status : String)
}

class ChatPresenter(
    private val view : ChatView,
    private val db : ChatDatabase,
    private val cache : CacheHelper
    ) {

    private val model = ChatModel(db, cache)

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun getMessages(){

        scopeMain.launch {

            val messages = withContext(Dispatchers.IO){

                model.getAllMessage()

            }

            view.messages(messages)

        }

    }

    fun userSendMessage(
        message : String,
    ){

        view.messageStatus("pending")

        scopeIO.launch{

            model.dbUserInsert(message)

            model.sendMessageToServer(userMessage = ChatMessage(user_message = message), onSuccess = { item ->
                val aiMessage = item.ai_message

                //Log.d("ai", aiMessage)

                model.dbAiInsert(msg = aiMessage)

                scopeMain.launch{

                    view.messages(model.getAllMessage())
                    view.messageStatus("success")



                }

            })

            withContext(Dispatchers.Main){

                view.messages(model.getAllMessage())

            }

        }

    }

    fun deleteMessage(id : Int){

        scopeIO.launch{

            val deleted = model.dbDeleteSingleMessage(id)

            withContext(Dispatchers.Main){

                if (deleted){

                    view.deleteStatus(true, "মেসেজ ডিলিট হয়েছে")
                    view.messages(model.getAllMessage())

                }else{

                    view.deleteStatus(false, "ডিলিট হয়নি")

                }

            }

        }

    }

    fun deleteAllMessages(){

        scopeIO.launch {

            model.dbDeleteAllMessage()

            withContext(Dispatchers.Main){

                view.deleteStatus(true, "সব মেসেজ ডিলিট হয়েছে")
                view.messages(model.getAllMessage())

            }

        }

    }

    fun setCache(key : String, value : String){

        model.cacheSet(key, value)

        view.cacheStatus("showed")

    }

    fun getCache(key: String){

        val data = model.cacheGet(key)

        if (!data.isNullOrEmpty()){

            view.cacheStatus("showed")

        }else{

            view.cacheStatus("")

        }

    }

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }



}