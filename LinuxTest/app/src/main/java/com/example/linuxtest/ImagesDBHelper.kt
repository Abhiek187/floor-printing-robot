package com.example.linuxtest

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ImagesDBHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // Located in /data/user/0/com.example.linuxtest/files/databases
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ImagesDB.db"
        const val TABLE_NAME = "ImagesTable"
        const val COLUMN_ID = "_id" // hidden column
        const val COLUMN_NAME = "name"
        const val COLUMN_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, " +
                "$COLUMN_IMAGE TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getTable(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT $COLUMN_NAME, $COLUMN_IMAGE FROM $TABLE_NAME",
            null)
    }

    fun addImage(name: String, image: String) {
        val cv = ContentValues()
        cv.put(COLUMN_NAME, name)
        cv.put(COLUMN_IMAGE, image)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, cv)
        db.close()
    }

    fun updateImage(name: String, image: String) {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_IMAGE FROM $TABLE_NAME " +
                "WHERE $COLUMN_NAME = '$name'", null)

        if (cursor.moveToFirst()) {
            // Should only be one result
            val oldImg = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE))
            val cv = ContentValues()
            cv.put(COLUMN_IMAGE, image)
            db.update(TABLE_NAME, cv, "$COLUMN_IMAGE =?", arrayOf(oldImg))
        }

        cursor.close()
        db.close()
    }
}
