package com.example.linuxtest.image

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ImagesTable")
data class Image(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "uri") val uri: String
)
