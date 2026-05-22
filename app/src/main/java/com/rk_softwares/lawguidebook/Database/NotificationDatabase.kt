package com.rk_softwares.lawguidebook.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rk_softwares.lawguidebook.Model.Items
import com.rk_softwares.lawguidebook.Model.NotificationData

class NotificationDatabase(
    val context: Context
) : SQLiteOpenHelper(context, "notification.db", null, 1) {

    private companion object{

        const val TABLE_NAME = "notification"
        const val NOTIFICATION_ID = "notification_id"
        const val NOTIFICATION_TITLE = "title"
        const val NOTIFICATION_DESCRIPTION = "description"
        const val IS_SEEN = "is_seen"

    }
    private lateinit var db : SQLiteDatabase


    override fun onCreate(db: SQLiteDatabase?) {

        val create_sql = "CREATE TABLE $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT, $NOTIFICATION_ID TEXT, $NOTIFICATION_TITLE TEXT, $NOTIFICATION_DESCRIPTION TEXT ,$IS_SEEN INTEGER)"

        db?.execSQL(create_sql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        /*
        val new_item = "";

        val upgrade_sql = "AFTER TABLE $TABLE_NAME ADD COLUMN $new_item"

        if (oldVersion < 2){

            db?.execSQL(upgrade_sql)

        }

         */

    }

    //====================================
    //Insert notification id and is_seen
    //====================================

    fun insert(id : String, title : String, description : String, isSeen : Boolean) {

        if (id.isEmpty() || title.isEmpty() || description.isEmpty()) return

        if (checkDuplicate(id)) return

        val db = dbOpen(true)

        try {

            val cv = ContentValues()

            cv.put(NOTIFICATION_ID, id)
            cv.put(NOTIFICATION_TITLE, title)
            cv.put(NOTIFICATION_DESCRIPTION, description)
            cv.put(IS_SEEN, if (isSeen) 1 else 0)

            db.insert(TABLE_NAME, null, cv)

        }catch (e : Exception){

            e.printStackTrace()

        }


    }//fun end

    //====================================
    //Getting all notification
    //====================================

    fun getAll() : List<NotificationData>{

        val db = dbOpen()

        var cursor : Cursor? = null

        val list : MutableList<NotificationData> = mutableListOf()

        try {

            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY id DESC", null)

            while (cursor.moveToNext()){

                val id = cursor.getString(cursor.getColumnIndexOrThrow(NOTIFICATION_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(NOTIFICATION_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(NOTIFICATION_DESCRIPTION))
                val is_new = cursor.getInt(cursor.getColumnIndexOrThrow(IS_SEEN))

                list.add(NotificationData(
                    id = id,
                    title = title,
                    description = description,
                    isNew = if (is_new == 1) true else false
                ))

            }

        }catch (e : Exception){

            e.printStackTrace()

        }finally {
            cursor?.close()
        }

        return list

    }//fun end

    //========================================
    //Deleting notification via id
    //========================================

    fun deleteOne(id: String) : Boolean{

        if (id.isEmpty()) return false

        val db = dbOpen(true)

        return try {

            db.delete(TABLE_NAME, "$NOTIFICATION_ID = ?", arrayOf(id)) > 0

        }catch (e : Exception){

            e.printStackTrace()
            false

        }

    }//fun end

    //========================================
    //Delete all notification data
    //========================================

    fun deleteAll(){

        val db = dbOpen(true)

        try {

            db.delete(TABLE_NAME, null, null)

        }catch (e : Exception){

            e.printStackTrace()

        }

    }//fun end

    //=================================================
    //Check notification is checked or not for red dot
    //=================================================

    fun hasUnseenNotification() : Boolean{

        val db = dbOpen()

        var hasUnseen = false

        var cursor : Cursor? = null

        try {

            cursor = db.rawQuery("SELECT $IS_SEEN FROM $TABLE_NAME", null)

            while (cursor.moveToNext()){

                val isSeen = cursor.getInt(cursor.getColumnIndexOrThrow(IS_SEEN))

                if (isSeen == 0){

                    hasUnseen = false
                    break

                }else{

                    hasUnseen = true

                }

            }

        }catch (e : Exception){

            e.printStackTrace()

        }finally {

            cursor?.close()

        }

        return hasUnseen

    }//fun end

    //==========================================
    //Update notification seen status
    //==========================================

    fun updateNotificationStatus(id: String){

        if (id.isEmpty()) return

        val isDataAvailable = checkDuplicate(id)

        val db = dbOpen(true)

        try {

            val cv = ContentValues()

            if (isDataAvailable){

                cv.put(IS_SEEN, 1)

                db.update(TABLE_NAME, cv, "WHERE $NOTIFICATION_ID = ?", arrayOf(id))

            }

        }catch (e : Exception){

            e.printStackTrace()

        }


    }

    private fun checkDuplicate(id: String): Boolean{

        if (id.isEmpty()) return false

        val db = dbOpen()

        var cursor : Cursor? = null

        var exists = false

        try {

            cursor = db.rawQuery("SELECT $NOTIFICATION_ID FROM $TABLE_NAME WHERE $NOTIFICATION_ID = ?", arrayOf(id))

            if (cursor.moveToFirst()){

                exists = true

            }

        }catch (e : Exception){

            e.printStackTrace()

        }finally {
            cursor?.close()
        }

        return exists

    }//fun end

    private fun dbOpen(writeable : Boolean = false) : SQLiteDatabase{

        if (!::db.isInitialized || !db.isOpen){

            db = if (writeable) writableDatabase else readableDatabase

        }

        return db

    }//fun end

}//class==================================================