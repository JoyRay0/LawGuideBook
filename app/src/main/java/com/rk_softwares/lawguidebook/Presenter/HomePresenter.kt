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

    fun onHistoryList (list : List<Items>)
    fun onSearchList (list: List<Items>)
    fun onCategoryList (list: List<Items>)
    fun onBookmarkList (list: List<Items>)
    fun onCalculationList (list: List<Items>)
    fun onSearchSuggestionList(list: List<Items>)

    fun serverStatus(message : String)
    fun message(status : String)

    fun appUpdateStatus(version: String)


}

class HomePresenter(
    private val view : Home,
    private val historyDB : HistoryDatabase? = null,
    private val bookmarkDB: BookmarkDatabase? = null
) {

    private val homeModel = HomeModel(historyDB, bookmarkDB)

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun getAllHistory(){

        scopeMain.launch{

            val items = withContext(Dispatchers.IO){

                homeModel.dbGetAllHistory()
            }

            view.onHistoryList(items)

        }

    }//fun end

    fun dbDeleteAllHistory(){

        scopeIO.launch {

            homeModel.dbHistoryDeleteAll()

            withContext(Dispatchers.Main){

                view.message("ডিলিট হয়েছে")

            }

        }

    }

    fun getAllBookmark(){

        scopeIO.launch {

            val data = homeModel.dbGetAllBookmark()

            withContext(Dispatchers.Main){

                view.onBookmarkList(data)

            }

        }

    }

    fun insertBookmark(title: String){

        if (title.isEmpty()) return

        scopeIO.launch {

            homeModel.dbAddBookmark(title)

            withContext(Dispatchers.Main){

                view.message("সেভ হয়েছে")
                view.onBookmarkList(homeModel.dbGetAllBookmark())

            }

        }

    }

    fun deleteOneBookmarkItem(title: String){

        if (title.isEmpty()) return

        scopeIO.launch {

            val deleted = homeModel.dbBookmarkDeleteOne(title) ?: false

            if (deleted){

                withContext(Dispatchers.Main){

                    view.message("ডিলিট হয়েছে")
                    view.onBookmarkList(homeModel.dbGetAllBookmark())

                }

            }else{
                view.message("ডিলিট হয়নি")
            }



        }

    }

    fun searchAndHistoryToServer(title : String){

        if (title.isEmpty()) return

        view.serverStatus("search_pending")

        scopeIO.launch {

            homeModel.dbAddHistory(title)

            withContext(Dispatchers.Main){

                view.onHistoryList(homeModel.dbGetAllHistory())

            }

            homeModel.searchDataToServer(
                items = Items(search = title),
                onSuccess = { result ->

                scopeMain.launch{

                    view.serverStatus("search_success")
                    view.onSearchList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch{

                        view.serverStatus("search_failed")

                    }

                }

            })

        }

    }//fun end

    fun homeItemFromServer(){

        view.serverStatus("home_item_pending")

        scopeIO.launch {

            homeModel.homeServer(
                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus("home_item_success")

                    }

                },
                onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            view.serverStatus("home_item_failed")

                        }

                    }

                }
            )

        }

    }//fun end

    fun categoryItemFromServer(){

        view.serverStatus("category_pending")

        scopeIO.launch {

            homeModel.categoryServer(onSuccess = { result ->

                scopeMain.launch {

                    view.serverStatus("category_success")
                    view.onCategoryList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus("category_failed")

                    }

                }


            })

        }

    }//fun end

    fun calculationLimitItemFromServer(){

        view.serverStatus("calculation_limit_pending")

        scopeIO.launch {

            homeModel.calculationLimitItemFrommServer (onSuccess = { result ->

                scopeMain.launch {

                    view.serverStatus("calculation_limit_success")
                    view.onCalculationList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus("calculation_limit_failed")

                    }

                }


            })

        }

    }//fun end

    fun appUpdate(){

        view.serverStatus("appUpdate_pending")

        scopeIO.launch {

            homeModel.appVersion(
                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus("appUpdate_success")
                        view.appUpdateStatus(result)

                    }

                },
                onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            view.serverStatus("appUpdate_failed")

                        }

                    }

                }
            )

        }

    }//fun end

    fun searchSuggestion(char : String){

        view.serverStatus("suggestion_pending")

        scopeIO.launch {

            homeModel.searchSuggestion(

                items  = Items(search = char),

                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus("suggestion_success")
                        view.onSearchSuggestionList(result)

                    }

                },
                onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            view.serverStatus("suggestion_failed")

                        }

                    }

                }
            )

        }

    }//fun end

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }
}