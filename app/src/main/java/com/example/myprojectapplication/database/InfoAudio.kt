package com.example.myprojectapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info_audio")
data class InfoAudio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val longitude: Double,
    val latitude: Double,
    val bpm: Int,
    val danceability: Double,
    val loudness: Double,
    val mood: String,
    val genre: String,
    val instrument: String,
    val audioFilePath: String
)
