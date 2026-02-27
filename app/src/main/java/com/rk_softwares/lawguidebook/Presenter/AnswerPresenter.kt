package com.rk_softwares.lawguidebook.Presenter

import com.rk_softwares.lawguidebook.Model.AnswerData
import com.rk_softwares.lawguidebook.Model.AnswerModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

interface Answer{

    fun answer(answer: AnswerData)
    fun serverStatus(message : String)

}

class AnswerPresenter(
    private val view : Answer
) {

    private val model = AnswerModel()

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())
    fun answerFromServer(question : String){

        CoroutineScope(Dispatchers.IO).launch {

            model.answer(
                answerData = AnswerData(question = question),

                onSuccess = { result ->

                    CoroutineScope(Dispatchers.Main).launch {

                        view.serverStatus("Success")
                        view.answer(result)

                    }

                },
                onFailed = {

                    CoroutineScope(Dispatchers.Main).launch {

                        view.serverStatus("Failed")

                    }

                }
            )

        }

    }

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }

}