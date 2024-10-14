package com.dicoding.asclepius.view.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.AppRepository
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import kotlinx.coroutines.launch

class ResultViewModel (private val repository: AppRepository) : ViewModel() {

    val history: LiveData<Results<List<HistoryEntity>>> = repository.getHistory()

    suspend fun isHistoryExists(imageUri: String): Boolean {
        return repository.isHistoryExists(imageUri)
    }

    fun insertHistory(history: List<HistoryEntity>) = viewModelScope.launch {
        repository.insertHistory(history)
    }
}