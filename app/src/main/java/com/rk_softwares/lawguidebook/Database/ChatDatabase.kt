package com.rk_softwares.lawguidebook.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ChatDatabase(
    val context: Context
) : SQLiteOpenHelper(context, "chat.db", null, 1)  {

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int
    ) {
        TODO("Not yet implemented")
    }


}