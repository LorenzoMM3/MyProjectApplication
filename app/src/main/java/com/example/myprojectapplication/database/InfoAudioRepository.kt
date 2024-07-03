package com.example.myprojectapplication.repository

import androidx.lifecycle.LiveData
import com.example.myprojectapplication.database.InfoAudio
import com.example.myprojectapplication.database.InfoAudioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InfoAudioRepository(private val infoAudioDao: InfoAudioDao) {

    val allInfoAudio: LiveData<List<InfoAudio>> = infoAudioDao.getAll()

    suspend fun insert(infoAudio: InfoAudio) {
        infoAudioDao.insert(infoAudio)
    }

    suspend fun delete(latitude: Double, longitude: Double) {
        withContext(Dispatchers.IO) {
            infoAudioDao.delete(latitude, longitude)
        }
    }

    suspend fun deleteAll() {
        infoAudioDao.deleteAll()
    }
}