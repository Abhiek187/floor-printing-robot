package com.example.linuxtest.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.linuxtest.image.Image

@Database(entities = [Image::class], version = 1, exportSchema = false)
abstract class ImageDatabase: RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        // Singleton
        @Volatile
        private var instance: ImageDatabase? = null

        fun getInstance(context: Context): ImageDatabase {
            // Don't create multiple instances of the database
            return if (instance != null) {
                instance!!
            } else {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context, ImageDatabase::class.java, "ImageDatabase"
                    ).build()
                    instance!!
                }
            }
        }
    }
}
