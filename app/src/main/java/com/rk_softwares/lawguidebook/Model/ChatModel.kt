package com.rk_softwares.lawguidebook.Model

data class ChatModel(

    val id : Long = 0L,
    val user_message : String = "",
    val ai_message : String = "",
    val isUser : Boolean = false,
    val deviceId : String = ""

)

