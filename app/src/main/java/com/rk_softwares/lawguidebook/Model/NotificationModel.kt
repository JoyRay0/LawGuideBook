package com.rk_softwares.lawguidebook.Model

import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Database.NotificationDatabase
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.OkHttpWrapper

data class Notification(

    @SerializedName("status")
    val status : String = "",

    @SerializedName("message")
    val message : String = "",

    @SerializedName("data")
    val data : List<NotificationData> = emptyList()

)

data class NotificationData(
    @SerializedName("id")
    val id : String = "",

    @SerializedName("title")
    val title : String = "",

    @SerializedName("description")
    val description : String = "",

    @SerializedName("is_new")
    val isNew : Boolean = false,

    @SerializedName("is_seen")
    val isSeen : Boolean = false

)

class NotificationModel(
    private val notificationDB : NotificationDatabase? = null
) {

    //Notification list for SQLite
    private val notificationList = mutableListOf<NotificationData>()

    fun notificationFromServer(
        onSuccess : (List<NotificationData>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getNotification())
            .execute(Notification::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.data)

                }else{

                    onFailed(true)

                }

            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

    fun getAllNotification() : List<NotificationData>{

        val data = notificationDB?.getAll() ?: emptyList()

        notificationList.clear()
        notificationList.addAll(data)

        return notificationList

    }

    fun insertNotification(id: String, title: String, description: String, isNew: Boolean){

        notificationDB?.insert(id, title, description, isNew)

    }

    fun deleteNotification(id: String) : Boolean?{

        return notificationDB?.deleteOne(id)

    }

    fun deleteAllNotification(){

        notificationDB?.deleteAll()

    }

    fun isNotificationSeen() : Boolean?{

        return notificationDB?.hasUnseenNotification()

    }

    fun updateNotificationStatus(id: String){

        notificationDB?.updateNotificationStatus(id)

    }

    fun updateAllNotificationStatus(){

        notificationDB?.updateAllNotificationStatus()

    }

}