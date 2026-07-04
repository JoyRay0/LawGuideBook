package com.rk_softwares.lawguidebook.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rk_softwares.lawguidebook.Model.QuizData

class QuizDatabase(val context: Context) : SQLiteOpenHelper(context, "quiz.db", null, 1) {

    private companion object{

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
$USER_INPUT TEXT DEFAULT 0)""".trimIndent()

        db?.execSQL(sql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insert(
        title: String,
        optionA : String,
        optionB : String,
        optionC : String,
        optionD : String,
        answer : String,
    ){

        if (title.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()
            || answer.isEmpty()){

            return

        }

        if (checkDuplicate(title)) return

        val db = dbOpen(true)

        val cv = ContentValues()

        try {

            cv.put(TITLE, title)
            cv.put(OPTIONS_A, optionA)
            cv.put(OPTIONS_B, optionB)
            cv.put(OPTIONS_C, optionC)
            cv.put(OPTIONS_D, optionD)
            cv.put(ANSWER, answer)

            db.insert(TABLE_NAME, null, cv)

        }catch (e : Exception){

            e.printStackTrace()

        }


    }//fun end

    fun getAll(): List<QuizData>{

        val db = dbOpen()

        val quizList : MutableList<QuizData> = mutableListOf()

        var cursor : Cursor? = null

        try {

            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

            while (cursor.moveToNext()){

                val title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                val optionA = cursor.getString(cursor.getColumnIndexOrThrow(OPTIONS_A))
                val optionB = cursor.getString(cursor.getColumnIndexOrThrow(OPTIONS_B))
                val optionC = cursor.getString(cursor.getColumnIndexOrThrow(OPTIONS_C))
                val optionD = cursor.getString(cursor.getColumnIndexOrThrow(OPTIONS_D))
                val answer = cursor.getString(cursor.getColumnIndexOrThrow(ANSWER))
                val userInput = cursor.getInt(cursor.getColumnIndexOrThrow(USER_INPUT))

                quizList.add(QuizData(
                    title = title,
                    optionA = optionA,
                    optionB = optionB,
                    optionC = optionC,
                    optionD = optionD,
                    answer = answer,
                    userInput = userInput
                ))

            }

        }catch (e : Exception){

            e.printStackTrace()

        }finally {
            cursor?.close()
        }

        return quizList

    }//fun end

    fun deleteAll(){

        val db = dbOpen(true)

        try {

            db.delete(TABLE_NAME, null, null)

        }catch (e : Exception){

            e.printStackTrace()

        }

    }//fun end

    fun updateQuiz(
        title: String,
        userSelectedItem : Int
    ){

        if (title.isEmpty() || userSelectedItem > 0) return

        if (!checkDuplicate(title)) return

        val db = dbOpen(true)

        val cv = ContentValues()

        try {

            cv.put(TITLE, title)
            cv.put(USER_INPUT, userSelectedItem)

            db.update(TABLE_NAME, cv, "$TITLE = ?", arrayOf(title))

        }catch (e : Exception){

            e.printStackTrace()

        }

    }//fun end

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