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

enum class ChatStatus(val value : String){

    ChatPending("chat_pending"),
    ChatSuccess("chat_success"),
    ChatFailed("chat_failed"),
    ChatCacheShowed("showed"),
    ChatCacheNotShowed("")

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

        if (message.isEmpty()) return

        view.messageStatus(ChatStatus.ChatPending.value)

        scopeIO.launch{

            model.dbUserInsert(message)

            model.sendMessageToServer(userMessage = ChatMessage(user_message = message),
                onSuccess = { item ->
                val aiMessage = item.ai_message

                model.dbAiInsert(msg = aiMessage)

                scopeMain.launch{

                    view.messages(model.getAllMessage())
                    view.messageStatus(ChatStatus.ChatSuccess.value)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch{

                        view.messageStatus(ChatStatus.ChatFailed.value)

                    }

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

        view.cacheStatus(ChatStatus.ChatCacheShowed.value)

    }

    fun getCache(key: String){

        val data = model.cacheGet(key)

        if (!data.isNullOrEmpty()){

            view.cacheStatus(ChatStatus.ChatCacheShowed.value)

        }else{

            view.cacheStatus(ChatStatus.ChatCacheNotShowed.value)

        }

    }

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }



}