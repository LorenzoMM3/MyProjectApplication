package com.example.myprojectapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UploadDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(uploadData: UploadData)

    @Query("DELETE FROM upload_data WHERE username = :username AND latitude = :latitude AND longitude = :longitude")
    fun delete(username: String, latitude: Double, longitude: Double)

    @Query("DELETE FROM upload_data")
    fun deleteAll()

    @Query("SELECT * FROM upload_data WHERE username = :username AND latitude = :latitude AND longitude = :longitude")
    fun getUploadData(username: String, latitude: Double, longitude: Double): LiveData<UploadData?>

    @Query("SELECT * FROM upload_data")
    fun getAllUploadData(): LiveData<List<UploadData>>

    @Query("SELECT * FROM upload_data")
    fun getAllUploadDataBlocking(): List<UploadData>
}
