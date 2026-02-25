package com.rk_softwares.lawguidebook.Presenter

import com.rk_softwares.lawguidebook.Database.ChatDatabase
import com.rk_softwares.lawguidebook.Helper.CacheHelper
import com.rk_softwares.lawguidebook.Model.ChatMessage
import com.rk_softwares.lawguidebook.Model.ChatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private var aiMessage : String = ""

    fun getMessages(){

        CoroutineScope(Dispatchers.Main).launch {

            val messages = withContext(Dispatchers.IO){

                model.getAllMessage()

            }

            view.messages(messages)

        }

    }

    fun sendMessage(
        message : String,
        uMessage : ChatMessage? = null,
        ){

        view.messageStatus("pending")

        CoroutineScope(Dispatchers.IO).launch{

            model.dbUserInsert(message)

            withContext(Dispatchers.Main){

                view.messages(model.getAllMessage())

            }

            model.sendMessageToServer(userMessage = uMessage, onSuccess = { item ->
                aiMessage = item.ai_message

                model.dbAiInsert(msg = aiMessage)

                CoroutineScope(Dispatchers.Main).launch{

                    view.messages(model.getAllMessage())
                    view.messageStatus("success")

                }

            })

        }

    }

    fun deleteMessage(id : Int){

        CoroutineScope(Dispatchers.IO).launch{

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

        CoroutineScope(Dispatchers.IO).launch {

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



}