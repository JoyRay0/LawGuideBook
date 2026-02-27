package com.rk_softwares.lawguidebook.Presenter

import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
import com.rk_softwares.lawguidebook.Model.Data
import com.rk_softwares.lawguidebook.Model.QuestionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface Questions{

    fun questionList(list: List<Data>)
    fun serverStatus(message : String)
    fun dbStatus(status: String)

}

class QuestionPresenter(
    private val view : Questions,
    private val db : BookmarkDatabase
){

    private val model = QuestionModel(db)

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun dbInsert(question : String){

        scopeIO.launch {

            model.dbInsert(question)

            withContext(Dispatchers.Main){

                view.dbStatus("সেভ হয়েছে")

            }


        }

    }

    fun questionsFromServer(category : String){

        scopeIO.launch{

            model.allQuestions(
                qData = Data(category = category),

                onSuccess = { result ->

                scopeMain.launch {

                    view.serverStatus("Success")
                    view.questionList(result)

                }

            }, onFailed = {

                scopeMain.launch {

                    view.serverStatus("Failed")

                }

            })

        }

    }

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }

}