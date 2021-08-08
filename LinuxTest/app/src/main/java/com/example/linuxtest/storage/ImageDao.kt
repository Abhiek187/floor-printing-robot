package com.example.linuxtest.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.linuxtest.image.Image

// Data Access Object
@Dao
interface ImageDao {
    @Query("SELECT * FROM ImagesTable")
    fun getSaves(): LiveData<List<Image>>

    // Alert the user when they save an image with the same name
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addImage(image: Image)
}
