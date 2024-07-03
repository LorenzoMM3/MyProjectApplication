package com.example.myprojectapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myprojectapplication.repository.InfoAudioRepository

class InfoAudioViewModelFactory(private val repository: InfoAudioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoAudioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InfoAudioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
