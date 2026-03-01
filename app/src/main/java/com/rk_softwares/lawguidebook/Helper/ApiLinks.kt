package com.rk_softwares.lawguidebook.Helper

object ApiLinks {

    private val main_link ="https://rksoftwares.fun/"

    fun getChatLink() : String { return main_link+"All_app/test.php" }

    fun getCategoryLink() : String { return main_link+"All_app/category.php" }

    fun getSearchLink() : String { return main_link+"All_app/search.php" }

    fun getQuestionLink() : String { return main_link+"All_app/question_test.php" }

    fun getAnswerLink() : String { return main_link+"All_app/answer_test.php" }

}