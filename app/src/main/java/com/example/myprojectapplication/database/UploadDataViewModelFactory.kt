package com.example.myprojectapplication.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UploadDataViewModelFactory(private val repository: UploadDataRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UploadDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
