package com.rk_softwares.lawguidebook.Presenter

import com.rk_softwares.lawguidebook.Database.HistoryDatabase
import com.rk_softwares.lawguidebook.Model.HomeModel
import com.rk_softwares.lawguidebook.Model.Items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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


    fun getAllHistory(){

        CoroutineScope(Dispatchers.Main).launch{

            val items = withContext(Dispatchers.IO){

                homeModel.getAllHistory()
            }

            view.historyList(items)

        }

    }

    fun historyToServer(
        title : String,
        ){

        CoroutineScope(Dispatchers.IO).launch {

            homeModel.dbAddHistory(title)

            withContext(Dispatchers.Main){

                view.historyList(homeModel.getAllHistory())

            }

            homeModel.searchDataToServer(
                items = Items(title = title),
                onSuccess = { result ->

                CoroutineScope(Dispatchers.Main).launch{

                    view.serverStatus("Success")
                    view.searchList(result)

                }

            }, onFailed = {

                CoroutineScope(Dispatchers.Main).launch{

                    view.serverStatus("Failed")

                }

            })

        }

    }


}