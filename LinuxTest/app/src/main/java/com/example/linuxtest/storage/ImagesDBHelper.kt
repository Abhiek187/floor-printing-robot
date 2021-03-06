package com.example.linuxtest.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.linuxtest.image.Image

class ImagesDBHelper(context: Context):
    SQLiteOpenHelper(context,
        DATABASE_NAME, null,
        DATABASE_VERSION
    ) {
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

    fun getSaves(): ArrayList<Image> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_NAME, $COLUMN_IMAGE FROM $TABLE_NAME",
            null)
        val saves = arrayListOf<Image>()

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE))
                saves.add(Image(name, image))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return saves
    }

    fun addImage(name: String, image: String) {
        val cv = ContentValues()
        cv.put(COLUMN_NAME, name)
        cv.put(COLUMN_IMAGE, image)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, cv)
        db.close()
    }
}
