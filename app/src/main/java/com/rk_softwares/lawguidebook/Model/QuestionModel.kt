package com.rk_softwares.lawguidebook.Model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
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

data class Question(

    val status : String = "",

    val message : String = "",

    val data : List<Data> = emptyList()
)

data class Data(
    val question : String = "",
    val category : String = ""
)

class QuestionModel(
    private val db : BookmarkDatabase
) {

    fun dbInsert(title : String){

        db.insert(title)

    }

    suspend fun allQuestions(
        qData : Data? = null,
        onSuccess : (List<Data>) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        if (qData == null) return

        val client = OkHttpClient()

        val gson = Gson()

        val body : RequestBody = gson.toJson(qData.category).toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request
            .Builder()
            .url(ApiLinks.getQuestionLink())
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

                        val data = gson.fromJson(data, Question::class.java)

                        if (data.status == "Success"){

                            onSuccess(data.data)
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

    }//fun end

}