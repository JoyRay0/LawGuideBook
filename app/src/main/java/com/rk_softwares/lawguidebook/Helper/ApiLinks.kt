package com.rk_softwares.lawguidebook.Helper

object ApiLinks {

    private val main_link ="https://lawguidebook.rksoftwares.fun/"

    fun getChatLink() : String { return main_link+"ai_chat" }

    fun getAllCategoryLink() : String { return main_link+"all_category" }

    fun getSearchLink() : String { return main_link+"search/" }

    fun getCategoryLink() : String { return main_link+"category/" }

    fun getAnswerLink() : String { return main_link+"answer" }
    
    fun getCalculationLimitLink() : String { return main_link+"calculation_limit" }

    fun getCalculationAllLink() : String { return main_link+"calculation_all" }

    fun getHomeItemLink() : String { return main_link+"" }

    fun getWebsitesLink() : String { return  main_link+"websites" }

    fun getAppUpdate() : String { return  main_link+"app_update" }

    fun getAds() : String { return  main_link+"ads" }

}