package com.rk_softwares.lawguidebook.Model

data class PostChatModel(

    val id : Long = 0L,
    val user_message : String = "",
    val ai_message : String = "",
    val isUser : Boolean = false,
    val deviceId : String = "",

)

data class ChatModel(

    val id : Int = 0,
    val message : String = "",
    val sender : String = "",
    val timestamp : String = ""

)

