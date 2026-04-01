package com.rk_softwares.lawguidebook.Model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
import com.rk_softwares.lawguidebook.Database.HistoryDatabase
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.OkHttpWrapper
import com.rk_softwares.lawguidebook.Helper.SecurityKey
import com.rk_softwares.lawguidebook.Helper.header
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.lang.Exception

data class Home(
    val status : String = "",
    val message : String = "",

    @SerializedName("items", alternate = ["item", "data"])
    val items : List<Items> = emptyList(),
    val version : String = ""
)

data class Items(
    val id : Int = 0,
    val image : String = "",
    val title : String = "",
    val question : String = "",
    val deviceId : String = "",
    val search : String = "",
    @SerializedName("t_name")
    val tableName : String = ""
)

class HomeModel(
    private val historyDB : HistoryDatabase? = null,
    private val bookmarkDB : BookmarkDatabase? = null
) {

    private val bookmarkList = mutableListOf<Items>()
    private val historyList = mutableListOf<Items>()

    fun dbGetAllHistory() : List<Items>{

        val data = historyDB?.getAll() ?: emptyList()

        historyList.clear()
        historyList.addAll(data)

        return historyList

    }

    fun dbAddHistory(title: String){

        historyDB?.insert(title)

    }

    fun dbHistoryDeleteAll(){

        historyDB?.deleteAll()

    }

    fun dbGetAllBookmark() : List<Items>{

        val data = bookmarkDB?.getAll() ?: emptyList()

        bookmarkList.clear()
        bookmarkList.addAll(data)

        return bookmarkList
    }

    fun dbAddBookmark(title: String){

        bookmarkDB?.insert(title)

    }

    fun dbBookmarkDeleteOne(title: String) : Boolean?{

        return bookmarkDB?.deleteOne(title)

    }


    fun homeServer(
        onSuccess : (List<Items>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){


        OkHttpWrapper()
            .url(ApiLinks.getHomeItemLink())
            .header()
            .execute(Home::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.items)

                }


            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

    fun categoryServer(
        onSuccess : (List<Items>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getAllCategoryLink())
            .header()
            .execute(Home::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.items)

                }


            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

    fun searchDataToServer(
        items : Items? = null,
        onSuccess : (List<Items>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        if (items == null) return

        OkHttpWrapper()
            .url(ApiLinks.getSearchLink()+1)
            .header()
            .post(items)
            .execute(Home::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.items)

                }


            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

    fun calculationLimitItemFrommServer(
        onSuccess : (List<Items>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getCalculationLimitLink())
            .header()
            .execute(Home::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.items)

                }


            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

    fun appVersion(
        onSuccess : (String) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getAppUpdate())
            .header()
            .execute(Home::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.version)

                }

            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end
}