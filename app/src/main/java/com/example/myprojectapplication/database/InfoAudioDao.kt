package com.example.myprojectapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InfoAudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(infoAudio: InfoAudio)

    @Query("DELETE FROM info_audio WHERE latitude = :latitude AND longitude = :longitude")
    fun delete(latitude: Double, longitude: Double)

    @Query("SELECT * FROM info_audio")
    fun getAll(): LiveData<List<InfoAudio>>

    @Query("DELETE FROM info_audio")
    fun deleteAll()
}
