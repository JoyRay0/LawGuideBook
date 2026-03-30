package com.rk_softwares.lawguidebook.Model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
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

data class Question(

    @SerializedName("status")
    val status : String = "",

    @SerializedName("message")
    val message : String = "",

    @SerializedName("data")
    val data : List<Data> = emptyList()
)

data class Data(
    @SerializedName("question")
    val question : String = "",

    @SerializedName("title")
    val title : String = "",

    @SerializedName("device_id")
    val deviceID : String = "",

    @SerializedName("t_name")
    val tableName : String = ""
)

class QuestionModel(
    private val db : BookmarkDatabase
) {

    fun dbInsert(title : String){

        db.insert(title)

    }

    fun allQuestions(
        qData : Data? = null,
        onSuccess : (List<Data>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        if (qData == null) return

        OkHttpWrapper()
            .url(ApiLinks.getCategoryLink()+1)
            .header()
            .post(qData)
            .execute(Question::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result.data)

                }

            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

}