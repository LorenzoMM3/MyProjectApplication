package com.example.myprojectapplication.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadDataRepository(private val uploadDataDao: UploadDataDao) {

    val allUploadData: LiveData<List<UploadData>> = uploadDataDao.getAllUploadData()

    suspend fun deleteAll() {
        uploadDataDao.deleteAll()
    }

    fun getUploadData(username: String, latitude: Double, longitude: Double): LiveData<UploadData?> {
        return uploadDataDao.getUploadData(username, latitude, longitude)
    }

    suspend fun getAllUploadDataBlocking(){
        withContext(Dispatchers.IO){
            uploadDataDao.getAllUploadDataBlocking()
        }
    }

    suspend fun insert(uploadData: UploadData) {
        uploadDataDao.insert(uploadData)
    }

    suspend fun delete(username: String, latitude: Double, longitude: Double) {
        withContext(Dispatchers.IO) {
            uploadDataDao.delete(username, latitude, longitude)
        }
    }
}
