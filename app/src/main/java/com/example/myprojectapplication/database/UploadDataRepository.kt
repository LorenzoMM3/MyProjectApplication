package com.example.myprojectapplication.database

import androidx.lifecycle.LiveData

class UploadDataRepository(private val uploadDataDao: UploadDataDao) {

    val allUploadData: LiveData<List<UploadData>> = uploadDataDao.getAllUploadData()

    fun getUploadData(username: String, latitude: Double, longitude: Double): LiveData<UploadData?> {
        return uploadDataDao.getUploadData(username, latitude, longitude)
    }

    suspend fun insert(uploadData: UploadData) {
        uploadDataDao.insert(uploadData)
    }

    suspend fun delete(username: String, latitude: Double, longitude: Double) {
        uploadDataDao.delete(username, latitude, longitude)
    }
}
