package com.rk_softwares.lawguidebook.Presenter

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
    fun notificationStatus (status: String)

}

class NotificationPresenter(
    private val view : Notification,
    private val notificationDB: NotificationDatabase? = null
) {

    private val model = NotificationModel(notificationDB)
    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun notificationFromServer(){

        view.serverStatus("notification_pending")

        scopeIO.launch {

            model.notificationFromServer(
                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus("notification_success")
                        view.notificationList(result)

                    }

                }, onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            view.serverStatus("notification_failed")

                        }

                    }

                })

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

    fun deleteNotification(data: NotificationData? = null){

        if (data == null) return

        scopeIO.launch {

            val isDeleted = model.deleteNotification(id = data.id) ?: false

            withContext(Dispatchers.Main){

                if (isDeleted){

                    view.notificationStatus("ডিলিট হয়েছে")
                    view.notificationList(model.getAllNotification())

                }else{

                    view.notificationStatus("ডিলিট হয়নি")

                }

            }

        }


    }//fun end

    fun deleteAllNotification(){

        scopeIO.launch {

            model.deleteAllNotification()

            withContext(Dispatchers.Main) {

                view.notificationStatus("সব ডিলিট হয়েছে")

            }

        }

    }//fun end

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }
}