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

enum class AnswerStatus(val value : String){

    AnswerPending("answer_pending"),
    AnswerSuccess("answer_success"),
    AnswerFailed("answer_failed")

}

class AnswerPresenter(
    private val view : Answer
) {

    private val model = AnswerModel()

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())
    fun answerFromServer(question : String){

        view.serverStatus(AnswerStatus.AnswerPending.value)

        scopeIO.launch {

            model.answer(
                answerData = AnswerData(question = question),

                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus(AnswerStatus.AnswerSuccess.value)
                        view.answer(result)

                    }

                },
                onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            view.serverStatus(AnswerStatus.AnswerFailed.value)

                        }

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