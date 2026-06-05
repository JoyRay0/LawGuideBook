package com.rk_softwares.lawguidebook.Presenter

import android.util.Log
import com.rk_softwares.lawguidebook.Database.NotificationDatabase
import com.rk_softwares.lawguidebook.Model.NotificationData
import com.rk_softwares.lawguidebook.Model.NotificationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface Notification{

    fun notificationList (list: List<NotificationData>)
    fun message (status: String)
    fun hasUnseenNotification (isSeen : Boolean)

}

class NotificationPresenter(
    private val view : Notification,
    private val notificationDB: NotificationDatabase? = null
) {

    private val model = NotificationModel(notificationDB)
    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun notificationFromServer(){

        scopeIO.launch {

            model.notificationFromServer(
                onSuccess = {}, onFailed = {})

        }

    }//fun end

    fun insertNotification(data: NotificationData? = null){

        if (data == null) return

        scopeIO.launch {

            model.insertNotification(
                id = data.id,
                title = data.title,
                description = data.description,
                isNew = data.isNew
            )

        }


    }//fun end

    fun getAllNotification(){

        scopeIO.launch {

            val data = model.getAllNotification()

            withContext(Dispatchers.Main){

                view.notificationList(data)

            }

        }

    }

    fun deleteNotification(id: String){

        if (id.isEmpty()) return

        scopeIO.launch {

            val isDeleted = model.deleteNotification(id = id) ?: false

            withContext(Dispatchers.Main){

                if (isDeleted){

                    view.message("ডিলিট হয়েছে")
                    view.notificationList(model.getAllNotification())

                }else{

                    view.message("ডিলিট হয়নি")

                }

            }

        }


    }//fun end

    fun deleteAllNotification(){

        scopeIO.launch {

            model.deleteAllNotification()

            withContext(Dispatchers.Main) {

                view.message("সব ডিলিট হয়েছে")
                view.notificationList(model.getAllNotification())

            }

        }

    }//fun end

    fun isNotificationSeen(){

        scopeIO.launch {

            val isSeen = model.isNotificationSeen()
            withContext(Dispatchers.Main){

                if (isSeen != null && isSeen){

                    view.hasUnseenNotification(isSeen = true)

                }else{

                    view.hasUnseenNotification(isSeen = false)

                }

            }

        }

    }//fun end

    fun updateNotificationStatus(id : String){

        if (id.isEmpty()) return

        scopeIO.launch {

            model.updateNotificationStatus(id)

            withContext(Dispatchers.Main){

                view.notificationList(model.getAllNotification())

            }

        }

    }//fun end

    fun updateAllNotificationStatus(){

        scopeIO.launch {

            model.updateAllNotificationStatus()

            withContext(Dispatchers.Main){

                view.notificationList(model.getAllNotification())

            }

        }

    }

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }
}