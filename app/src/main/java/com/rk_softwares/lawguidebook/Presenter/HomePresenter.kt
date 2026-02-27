package com.rk_softwares.lawguidebook.Presenter

import com.rk_softwares.lawguidebook.Database.HistoryDatabase
import com.rk_softwares.lawguidebook.Model.HomeModel
import com.rk_softwares.lawguidebook.Model.Items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface Home{

    fun historyList (list : List<Items>)
    fun searchList (list: List<Items>)
    fun categoryList (list: List<Items>)

    fun serverStatus(message : String)


}

class HomePresenter(
    private val view : Home,
    private val historyDB : HistoryDatabase
) {

    private val homeModel = HomeModel(historyDB)

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun getAllHistory(){

        scopeMain.launch{

            val items = withContext(Dispatchers.IO){

                homeModel.getAllHistory()
            }

            view.historyList(items)

        }

    }//fun end

    fun searchAndHistoryToServer(title : String){

        scopeIO.launch {

            homeModel.dbAddHistory(title)

            withContext(Dispatchers.Main){

                view.historyList(homeModel.getAllHistory())

            }

            homeModel.searchDataToServer(
                items = Items(title = title),
                onSuccess = { result ->

                scopeMain.launch{

                    view.serverStatus("Success")
                    view.searchList(result)

                }

            }, onFailed = {

                scopeMain.launch{

                    view.serverStatus("Failed")

                }

            })

        }

    }//fun end

    fun categoryItemFromServer(){

        scopeIO.launch {

            homeModel.categoryServer(onSuccess = { result ->

                view.serverStatus("Success")
                view.categoryList(result)

            }, onFailed = {

                view.serverStatus("Failed")
            })

        }

    }//fun end

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }
}