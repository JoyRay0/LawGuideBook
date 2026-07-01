package com.rk_softwares.lawguidebook.Database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class QuizDatabase(val context: Context) : SQLiteOpenHelper(context, "quiz.db", null, 1) {

    companion object{

        const val TABLE_NAME = "quiz"
        const val ID = "id"
        const val TITLE = "title"
        const val OPTIONS_A = "option_a"
        const val OPTIONS_B = "option_b"
        const val OPTIONS_C = "option_c"
        const val OPTIONS_D = "option_d"
        const val ANSWER = "answer"
        const val USER_INPUT = "user_input"

    }

    private lateinit var db : SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase?) {

        val sql = """CREATE TABLE IF NOT EXISTS $TABLE_NAME (
$ID INTEGER PRIMARY KEY AUTOINCREMENT, 
$TITLE TEXT NOT NULL, 
$OPTIONS_A TEXT NOT NULL, 
$OPTIONS_B TEXT NOT NULL, 
$OPTIONS_C TEXT NOT NULL, 
$OPTIONS_D TEXT NOT NULL, 
$ANSWER TEXT NOT NULL, 
$USER_INPUT TEXT DEFAULT NULL)""".trimIndent()

        db?.execSQL(sql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insert(){}//fun end

    fun getAll(){}//fun end

    fun deleteAll(){

        val db = dbOpen(true)

        try {

            db.delete(TABLE_NAME, null, null)

        }catch (e : Exception){

            e.printStackTrace()

        }

    }//fun end

    fun updateQuiz(){}//fun end

    private fun checkDuplicate(title : String) : Boolean{

        val db = dbOpen()

        var cursor : Cursor? = null

        var isExists = false

        try {

            cursor = db.rawQuery("SELECT $TITLE FROM $TABLE_NAME WHERE $TITLE = ?", arrayOf(title))

            if (cursor.moveToFirst()){

                isExists = true

            }

        }catch (e : Exception){
            e.printStackTrace()

        }finally {
            cursor?.close()
        }


        return isExists
    }//fun end

    private fun dbOpen(isWriteable : Boolean = false) : SQLiteDatabase{

        if (!::db.isInitialized && !db.isOpen){

            db = if (isWriteable) writableDatabase else readableDatabase

        }
        return db
    }

}