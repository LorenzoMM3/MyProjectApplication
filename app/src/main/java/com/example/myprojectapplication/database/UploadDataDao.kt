package com.example.myprojectapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UploadDataDao {

    @Insert
    fun insert(uploadData: UploadData)

    @Delete
    fun delete(uploadData: UploadData)

    @Query("SELECT * FROM upload_data WHERE username = :username AND latitude = :latitude AND longitude = :longitude")
    fun getUploadData(username: String, latitude: Double, longitude: Double): LiveData<UploadData?>

    @Query("SELECT * FROM upload_data")
    fun getAllUploadData(): LiveData<List<UploadData>>
}
