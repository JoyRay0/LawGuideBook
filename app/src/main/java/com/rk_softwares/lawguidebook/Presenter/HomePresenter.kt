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

enum class HomeStatus(val value : String){

    MessageDeleteSuccess("ডিলিট হয়েছে"),
    MessageDeleteFailed("ডিলিট হয়নি"),
    MessageSaved("সেভ হয়েছে"),

    SearchPending("search_pending"),
    SearchSuccess("search_success"),
    SearchFailed("search_failed"),

    CategoryPending("category_pending"),
    CategorySuccess("category_success"),
    CategoryFailed("category_failed"),

    CalculationLimitPending("calculation_limit_pending"),
    CalculationLimitSuccess("calculation_limit_success"),
    CalculationLimitFailed("calculation_limit_failed"),

    SuggestionPending("suggestion_pending"),
    SuggestionSuccess("suggestion_success"),
    SuggestionFailed("suggestion_failed"),
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

                view.message(HomeStatus.MessageDeleteSuccess.value)

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

                view.message(HomeStatus.MessageSaved.value)
                view.onBookmarkList(homeModel.dbGetAllBookmark())

            }

        }

    }

    fun deleteOneBookmarkItem(title: String){

        if (title.isEmpty()) return

        scopeIO.launch {

            val deleted = homeModel.dbBookmarkDeleteOne(title) ?: false

            withContext(Dispatchers.Main){

                if (deleted){

                    view.message(HomeStatus.MessageDeleteSuccess.value)
                    view.onBookmarkList(homeModel.dbGetAllBookmark())

                }else{
                    view.message(HomeStatus.MessageDeleteFailed.value)
                }

            }

        }

    }

    fun searchAndHistoryToServer(title : String){

        if (title.isEmpty()) return

        view.serverStatus(HomeStatus.SearchPending.value)

        scopeIO.launch {

            homeModel.dbAddHistory(title)

            withContext(Dispatchers.Main){

                view.onHistoryList(homeModel.dbGetAllHistory())

            }

            homeModel.searchDataToServer(
                items = Items(search = title),
                onSuccess = { result ->

                scopeMain.launch{

                    view.serverStatus(HomeStatus.SearchSuccess.value)
                    view.onSearchList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch{

                        view.serverStatus(HomeStatus.SearchFailed.value)

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

        view.serverStatus(HomeStatus.CategoryPending.value)

        scopeIO.launch {

            homeModel.categoryServer(onSuccess = { result ->

                scopeMain.launch {

                    view.serverStatus(HomeStatus.CategorySuccess.value)
                    view.onCategoryList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus(HomeStatus.CategoryFailed.value)

                    }

                }


            })

        }

    }//fun end

    fun calculationLimitItemFromServer(){

        view.serverStatus(HomeStatus.CalculationLimitPending.value)

        scopeIO.launch {

            homeModel.calculationLimitItemFrommServer (onSuccess = { result ->

                scopeMain.launch {

                    view.serverStatus(HomeStatus.CalculationLimitSuccess.value)
                    view.onCalculationList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus(HomeStatus.CalculationLimitFailed.value)

                    }

                }


            })

        }

    }//fun end

    fun appUpdate(){

        //view.serverStatus("appUpdate_pending")

        scopeIO.launch {

            homeModel.appVersion(
                onSuccess = { result ->

                    scopeMain.launch {

                        //view.serverStatus("appUpdate_success")
                        view.appUpdateStatus(result)

                    }

                },
                onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            //view.serverStatus("appUpdate_failed")

                        }

                    }

                }
            )

        }

    }//fun end

    fun searchSuggestion(char : String){

        view.serverStatus(HomeStatus.SuggestionPending.value)

        scopeIO.launch {

            homeModel.searchSuggestion(

                items  = Items(search = char),

                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus(HomeStatus.SuggestionSuccess.value)
                        view.onSearchSuggestionList(result)

                    }

                },
                onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            view.serverStatus(HomeStatus.SuggestionFailed.value)

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