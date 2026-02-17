package com.rk_softwares.lawguidebook.Database

import android.content.*
import android.database.Cursor
import android.database.sqlite.*
import com.rk_softwares.lawguidebook.Model.ItemList

class HistoryDatabase(
    val context: Context
) : SQLiteOpenHelper(context, "history.db", null, 1) {

    private val TABLE_NAME = "history"
    private lateinit var db: SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase?) {

        val create_sql = "CREATE TABLE $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT)"

        db?.execSQL(create_sql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        val update_sql = "DROP TABLE IF EXISTS $TABLE_NAME"

        db?.execSQL(update_sql)
        onCreate(db)

    }

    fun inset(title : String){

        val db = dbOpen(true)

        if (title.isEmpty()) return

        if (checkDuplicateData(title)) return

        try {

            val cv = ContentValues()

            cv.put("title", title)

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

            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

            while (cursor.moveToNext()){

                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))

                list.add(ItemList(question = title))

            }

        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            cursor?.close()
        }

        return list
    }

    private fun checkDuplicateData(title: String) : Boolean{

        if (title.isEmpty()) return false

        val db = dbOpen()

        var cursor : Cursor? = null

        var exists = false

        try {

            cursor = db.rawQuery("SELECT title FROM $TABLE_NAME WHERE title = ?", arrayOf(title))

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

    fun deleteAll(){

        val db = dbOpen(true)

        db.delete(TABLE_NAME, null, null)

    }

    private fun dbOpen(writable : Boolean = false) : SQLiteDatabase{

        if (!::db.isInitialized || !db.isOpen){

            db = if (writable) writableDatabase else readableDatabase

        }

        return db
    }

    fun closeDB(){ if (::db.isInitialized && db.isOpen) db.close() }

}