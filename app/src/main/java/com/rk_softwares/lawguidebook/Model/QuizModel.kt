package com.rk_softwares.lawguidebook.Model

import com.google.gson.annotations.SerializedName
import com.rk_softwares.lawguidebook.Database.QuizDatabase
import kotlinx.serialization.Serializable

data class Quiz(

    val status : String = "",
    val message : String = "",

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



}