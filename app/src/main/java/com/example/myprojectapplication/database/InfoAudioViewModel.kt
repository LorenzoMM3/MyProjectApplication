package com.example.myprojectapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myprojectapplication.database.InfoAudio
import com.example.myprojectapplication.repository.InfoAudioRepository
import kotlinx.coroutines.launch

class InfoAudioViewModel(private val repository: InfoAudioRepository) : ViewModel() {

    val allInfoAudio: LiveData<List<InfoAudio>> = repository.allInfoAudio

    fun insert(infoAudio: InfoAudio) = viewModelScope.launch {
        repository.insert(infoAudio)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun delete(latitude: Double, longitude: Double) = viewModelScope.launch {
        viewModelScope.launch {
            repository.delete(latitude, longitude)
        }
    }
}