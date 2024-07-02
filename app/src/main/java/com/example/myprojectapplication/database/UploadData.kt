package com.example.myprojectapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "upload_data", primaryKeys = ["username", "latitude", "longitude"])
data class UploadData(
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)
