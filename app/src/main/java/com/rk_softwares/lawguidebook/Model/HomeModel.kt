package com.rk_softwares.lawguidebook.Model

import com.google.gson.Gson
import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
import com.rk_softwares.lawguidebook.Database.HistoryDatabase
import com.rk_softwares.lawguidebook.Helper.ApiLinks
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
    val items : List<Items> = emptyList()
)

data class Items(
    val image : String = "",
    val title : String = "",
    val question : String = "",
    val deviceId : String = "",
    val search : String = ""
)

class HomeModel(
    private val historyDB : HistoryDatabase,
    private val bookmarkDB : BookmarkDatabase
) {

    private val bookmarkList = mutableListOf<Items>()
    private val historyList = mutableListOf<Items>()

    fun dbGetAllHistory() : List<Items>{

        val data = historyDB.getAll()

        historyList.clear()
        historyList.addAll(data)

        return historyList

    }

    fun dbAddHistory(title: String){

        historyDB.inset(title)

    }

    fun dbHistoryDeleteAll(){

        historyDB.deleteAll()

    }

    fun dbGetAllBookmark() : List<Items>{

        val data = bookmarkDB.getAll()

        bookmarkList.clear()
        bookmarkList.addAll(data)

        return bookmarkList
    }

    fun dbAddBookmark(title: String){

        bookmarkDB.insert(title)

    }

    fun dbBookmarkDeleteOne(title: String) : Boolean{

        return bookmarkDB.deleteOne(title)

    }


    fun categoryServer(
        //items : Items? = null,
        onSuccess : (List<Items>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        //if (items == null) return

        val client = OkHttpClient()

        val gson = Gson()

        val request = Request
            .Builder()
            .url(ApiLinks.getCategoryLink())
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                onFailed(true)

            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful){

                    val data = response.body.string()

                    try {

                        val data = gson.fromJson(data, Home::class.java)

                        if (data.status == "Success"){

                            onSuccess(data.items)
                            onFailed(false)

                        }else{

                            onFailed(true)
                        }


                    }catch (e : Exception){

                        e.printStackTrace()
                        onFailed(true)

                    }

                }else{

                    onFailed(true)

                }


            }
        })


    }

    fun searchDataToServer(
        items : Items? = null,
        onSuccess : (List<Items>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        if (items == null) return

        val client = OkHttpClient()

        val gson = Gson()

        val body : RequestBody = gson.toJson(items).toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request
            .Builder()
            .url(ApiLinks.getSearchLink())
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                onFailed(true)


            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful){

                    val data = response.body.string()

                    try {

                        val data = gson.fromJson(data, Home::class.java)

                        if (data.status == "Success"){

                            onSuccess(data.items)
                            onFailed(false)

                        }else{
                            onFailed(true)
                        }

                    }catch (e : Exception){

                        e.printStackTrace()
                        onFailed(true)

                    }

                }else{

                    onFailed(true)

                }


            }
        })

    }
}