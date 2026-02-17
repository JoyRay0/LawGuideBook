package com.rk_softwares.lawguidebook.Database

import android.content.*
import android.database.Cursor
import android.database.sqlite.*
import com.rk_softwares.lawguidebook.Model.ItemList

class BookmarkDatabase
    (val context : Context)
    : SQLiteOpenHelper(context, "bookmark.db", null, 2)
{

    private val TABLE_NAME = "bookmark"
    private lateinit var db : SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase?) {

        val create_sql = "CREATE TABLE $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT)"

        db?.execSQL(create_sql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        val update_sql = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(update_sql)

        onCreate(db)

    }

    fun insert(question: String){

        if (question.isEmpty()) return

        if (checkDuplicateData(question)) return

        val db = dbOpen(true)

        try {

            val cv = ContentValues()

            cv.put("question", question)

            db.insert(TABLE_NAME, null, cv)

        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    fun getAll() : List<ItemList>{

        val db = dbOpen()

        var cursor : Cursor? = null

        val list : MutableList<ItemList> = mutableListOf()

        try {

            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY id DESC", null)

            while (cursor.moveToNext()){

                val question = cursor.getString(cursor.getColumnIndexOrThrow("question"))

                list.add(ItemList(question = question))

            }

        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            cursor?.close()
        }

        return list
    }

    fun deleteOne(question: String) : Boolean{

        if (question.isEmpty()) return false

        val db = dbOpen(true)

        return try {

            db.delete(TABLE_NAME, "question = ?", arrayOf(question)) > 0

        }catch (e : Exception){

            e.printStackTrace()
            false

        }


    }

    fun closeDB(){ if (::db.isInitialized && db.isOpen) db.close() }

    private fun checkDuplicateData(question: String) : Boolean{

        if (question.isEmpty()) return false

        val db = dbOpen()

        var cursor : Cursor? = null

        var exists = false

        try {

            cursor = db.rawQuery("SELECT question FROM $TABLE_NAME WHERE question = ?", arrayOf(question))

            if (cursor.moveToFirst()){

                exists = true

            }

        }catch (e : Exception){

            e.printStackTrace()

        }finally {

            cursor?.close()
        }

        return exists
    }

    private fun dbOpen(writable : Boolean = false) : SQLiteDatabase{

        if (!::db.isInitialized || !db.isOpen){

            db = if (writable) writableDatabase else readableDatabase

        }

        return db
    }

}