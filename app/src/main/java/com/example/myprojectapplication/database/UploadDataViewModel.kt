package com.example.myprojectapplication.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadDataViewModel(private val repository: UploadDataRepository) : ViewModel() {

    val allUploadData: LiveData<List<UploadData>> = repository.allUploadData

    fun getUploadData(username: String, latitude: Double, longitude: Double): LiveData<UploadData?> {
        return repository.getUploadData(username, latitude, longitude)
    }

    fun insert(uploadData: UploadData) = viewModelScope.launch {
        repository.insert(uploadData)
    }

    fun getAllUploadDataBlocking(){
        viewModelScope.launch{
            repository.getAllUploadDataBlocking()
        }
    }

    fun delete(username: String, latitude: Double, longitude: Double) = viewModelScope.launch {
        viewModelScope.launch {
            repository.delete(username, latitude, longitude)
        }
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}
