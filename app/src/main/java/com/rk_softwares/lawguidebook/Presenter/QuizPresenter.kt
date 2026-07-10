package com.rk_softwares.lawguidebook.Presenter

import android.util.Log
import com.rk_softwares.lawguidebook.Database.QuizDatabase
import com.rk_softwares.lawguidebook.Model.QuizData
import com.rk_softwares.lawguidebook.Model.QuizModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface Quiz{

    fun quizList(list : List<QuizData>)
    fun quizCount(tQuiz : Int, cQuiz: Int)
    fun dbStatus(status : String)

}

enum class QuizStatus(val value : String){

    QuizPending("quiz_pending"),
    QuizSuccess("quiz_success"),
    QuizFailed("quiz_failed")

}

class QuizPresenter(
    private val view : Quiz,
    private val db : QuizDatabase
) {

    private val model = QuizModel(db)

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun quizFromServer() {

        scopeIO.launch {

            model.quizFromServer(
                onSuccess = {

                    val list = it.data
                    val version = it.quizVersion

                    /* local db call */

                    if (model.isNewVersion(version)){

                        model.deleteAllQuiz()

                        list.forEach { quiz ->

                            model.insertQuiz(
                                title = quiz.title,
                                optionA = quiz.optionA,
                                optionB = quiz.optionB,
                                optionC = quiz.optionC,
                                optionD = quiz.optionD,
                                answer = quiz.answer,
                                version = version
                            )

                        }

                    }

                },
                onFailed = {


                }
            )

        }

    }

    /*
    fun insert(
        title: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        answer: String,
        version: String
    ){

        scopeIO.launch {

            model.insertQuiz(
                title = title,
                optionA = optionA,
                optionB = optionB,
                optionC = optionC,
                optionD = optionD,
                answer = answer,
                version = version
            )

        }

    }

     */

    fun getAllQuiz(){

        view.dbStatus(QuizStatus.QuizPending.value)

        scopeIO.launch {

            val data = model.getAllQuizData()

            withContext(Dispatchers.Main){

                if (!data.isEmpty()) {

                    view.dbStatus(QuizStatus.QuizSuccess.value)

                }else{

                    view.dbStatus(QuizStatus.QuizFailed.value)

                }

                view.quizList(data)

            }

        }

    }

    fun updateQuiz(
        title: String,
        userInput: Int
    ){

        scopeIO.launch {

            model.updateQuzData(title = title, userInput = userInput)

            val data = model.getAllQuizData()

            withContext(Dispatchers.Main){

                view.quizList(data)

            }

        }

    }

    /*
    fun iSNewVersion(version: String){

        scopeIO.launch {

            val vData = model.isNewVersion(version)     /* Version data */

            withContext(Dispatchers.Main){

                view.newVersionStatus(vData)

            }

        }

    }

     */

    /*
    fun deleteAll(){

        scopeIO.launch {

            model.deleteAllQuiz()

        }

    }

     */

    fun quizCount(){

        var totalQuiz = 0
        var quizCompleted = 0

        scopeIO.launch {

            model.quizCount(tQuiz = {

                totalQuiz = it

            }, cQuiz = {

                quizCompleted = it

            })

            withContext(Dispatchers.Main){

                view.quizCount(tQuiz = totalQuiz, cQuiz = quizCompleted)

            }

        }

    }

    fun onDestroy(){

        scopeIO.cancel()
        scopeMain.cancel()

    }

}