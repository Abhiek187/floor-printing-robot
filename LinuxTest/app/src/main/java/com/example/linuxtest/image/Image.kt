package com.example.linuxtest.image

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ImagesTable")
data class Image(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "uri") val uri: String
) : Parcelable
