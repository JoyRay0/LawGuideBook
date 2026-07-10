package com.rk_softwares.lawguidebook.Model

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Database.QuizDatabase
import com.rk_softwares.lawguidebook.Helper.ApiLinks
import com.rk_softwares.lawguidebook.Helper.OkHttpWrapper
import com.rk_softwares.lawguidebook.Helper.header
import kotlinx.serialization.Serializable

data class Quiz(

    val status : String = "",
    val message : String = "",

    @SerializedName("version")
    val quizVersion : String = "",

    @SerializedName("data")
    val data : List<QuizData> = emptyList()

)

data class QuizData(

    val title : String = "",
    val optionA : String = "",
    val optionB : String = "",
    val optionC : String = "",
    val optionD : String = "",
    val answer : String = "",
    val userInput : Int = 0


)

class QuizModel(
    private val db : QuizDatabase
) {

    fun quizFromServer(
        onSuccess : (Quiz) -> Unit = {},
        onFailed : (Boolean) -> Unit = {}
    ){

        OkHttpWrapper()
            .url(ApiLinks.getQuiz())
            .header()
            .execute(Quiz::class.java, onSuccess = { result ->

                if (result.status == "Success"){

                    onSuccess(result)

                }else{

                    onFailed(true)

                }

            }, onFailed = {onFailed(it)}, onError = {})

    }//fun end

    fun insertQuiz(
        title: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        answer: String,
        version: String
    ){

        if (title.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()
            || answer.isEmpty() || version.isEmpty()){

            return

        }

        db.insert(
            title = title,
            optionA = optionA,
            optionB = optionB,
            optionC = optionC,
            optionD = optionD,
            answer = answer,
            version = version
        )

    }

    fun getAllQuizData(): List<QuizData>{

        val data = db.getAll()

        return data

    }

    fun updateQuzData(
        title: String,
        userInput: Int
    ){

        if (title.isEmpty() || userInput < 0) return

        db.updateQuiz(title = title, userSelectedItem = userInput)

    }

    fun isNewVersion(version: String): Boolean{

        if (version.isEmpty()) return false

        val isNew = db.isNewVersion(version)

        return isNew

    }

    fun deleteAllQuiz(){

        db.deleteAll()

    }

    fun quizCount(tQuiz : (Int) -> Unit, cQuiz : (Int) -> Unit){        /* tQuiz = Total Quiz, cQuiz = Completed quiz */

        db.quizCount( totalQuiz = { tQuiz(it) }, totalQuizCompleted = {cQuiz(it)} )

    }


}