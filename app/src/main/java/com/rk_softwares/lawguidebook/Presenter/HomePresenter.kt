package com.rk_softwares.lawguidebook.Presenter

import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
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
    fun bookmarkList (list: List<Items>)

    fun serverStatus(message : String)
    fun message(status : String)


}

class HomePresenter(
    private val view : Home,
    private val historyDB : HistoryDatabase,
    private val bookmarkDB: BookmarkDatabase
) {

    private val homeModel = HomeModel(historyDB, bookmarkDB)

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun getAllHistory(){

        scopeMain.launch{

            val items = withContext(Dispatchers.IO){

                homeModel.dbGetAllHistory()
            }

            view.historyList(items)

        }

    }//fun end

    fun getAllBookmark(){

        scopeIO.launch {

            val data = homeModel.dbGetAllBookmark()

            withContext(Dispatchers.Main){

                view.bookmarkList(data)

            }

        }

    }

    fun insertBookmark(title: String){

        if (title.isEmpty()) return

        scopeIO.launch {

            homeModel.dbAddBookmark(title)

            withContext(Dispatchers.Main){

                view.message("সেভ হয়েছে")
                view.bookmarkList(homeModel.dbGetAllBookmark())

            }

        }

    }

    fun deleteOneBookmarkItem(title: String){

        if (title.isEmpty()) return

        scopeIO.launch {

            val deleted = homeModel.dbBookmarkDeleteOne(title)

            if (deleted){

                withContext(Dispatchers.Main){

                    view.message("ডিলিট হয়েছে")
                    view.bookmarkList(homeModel.dbGetAllBookmark())

                }

            }else{
                view.message("ডিলিট হয়নি")
            }



        }

    }

    fun searchAndHistoryToServer(title : String){

        if (title.isEmpty()) return

        view.serverStatus("Pending")

        scopeIO.launch {

            homeModel.dbAddHistory(title)

            withContext(Dispatchers.Main){

                view.historyList(homeModel.dbGetAllHistory())

            }

            homeModel.searchDataToServer(
                items = Items(title = title),
                onSuccess = { result ->

                scopeMain.launch{

                    view.serverStatus("Success")
                    view.searchList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch{

                        view.serverStatus("Failed")

                    }

                }

            })

        }

    }//fun end

    fun categoryItemFromServer(){

        view.serverStatus("Pending")

        scopeIO.launch {

            homeModel.categoryServer(onSuccess = { result ->

                scopeMain.launch {

                    view.serverStatus("Success")
                    view.categoryList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus("Failed")

                    }

                }


            })

        }

    }//fun end

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }
}