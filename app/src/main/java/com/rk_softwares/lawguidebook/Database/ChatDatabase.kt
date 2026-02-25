package com.rk_softwares.lawguidebook.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rk_softwares.lawguidebook.Model.ChatMessage

class ChatDatabase(
    val context: Context
) : SQLiteOpenHelper(context, "chat.db", null, 6)  {

    private val TABLE_NAME = "chat_history"
    private lateinit var db: SQLiteDatabase

    companion object{
        const val COL_ID = "id"
        const val COL_MESSAGE = "message"
        const val COL_SENDER = "sender_type"
        const val COL_TIMESTAMP = "timestamp"

        const val COL_IS_USER = "is_user"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val create_sql = "CREATE TABLE $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT NOT NULL, sender_type TEXT NOT NULL, is_user INTEGER NOT NULL DEFAULT 0, timestamp TEXT NOT NULL)"

        db?.execSQL(create_sql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        /*
        val upgrade_sql = "AFTER TABLE $TABLE_NAME ADD COLUMN column_name TEXT DEFAULT NULL"
        if (oldVersion < newVersion) db?.execSQL(upgrade_sql)
         */
        val upgrade_sql = "DROP TABLE IF EXISTS $TABLE_NAME"

        db?.execSQL(upgrade_sql)

        onCreate(db)

    }

    fun insert(message : String, isUser : Boolean, messageType : String, timestamp: String){

        if (message.isEmpty() ||messageType.isEmpty() || timestamp.isEmpty()) return

        val booleanData = if (isUser) 1 else 0

        val db = dbOpen(true)

        val cv = ContentValues()

        try {

            cv.put(COL_MESSAGE, message)
            cv.put(COL_SENDER, messageType)
            cv.put(COL_IS_USER, booleanData)
            cv.put(COL_TIMESTAMP, timestamp)

            db.insert(TABLE_NAME, null, cv)

        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    fun getAll() : List<ChatMessage>{

        val db = dbOpen()

        var cursor : Cursor? = null

        val list : MutableList<ChatMessage> = mutableListOf()

        try {

            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME LIMIT 30 ", null)

            while (cursor.moveToNext()){

                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val message = cursor.getString(cursor.getColumnIndexOrThrow(COL_MESSAGE))
                val sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER))
                val is_user = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_USER))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP))

                list.add(ChatMessage(
                    id = id,
                    message = message,
                    sender = sender,
                    isUser = if (is_user == 1) true else false,
                    timestamp = timestamp
                ))

            }

        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            cursor?.close()
        }

        return list
    }

    fun deleteOne(id : Int) : Boolean {

        if (id <= 0) return false

        val db = dbOpen(true)

        return try {

            db.delete(TABLE_NAME, "id = ?", arrayOf(id.toString())) > 0

        } catch (e: Exception) {

            e.printStackTrace()
            false

        }
    }

    fun deleteAll(){

        val db = dbOpen(true)

        db.delete(TABLE_NAME, null, null)

    }

    //fun closeDB(){ if (::db.isInitialized && db.isOpen) db.close() }

    private fun dbOpen(writable : Boolean = false) : SQLiteDatabase{

        db = if (writable) writableDatabase else readableDatabase

        return db
    }

}