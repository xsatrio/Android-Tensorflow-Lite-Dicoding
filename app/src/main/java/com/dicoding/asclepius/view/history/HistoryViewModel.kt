package com.dicoding.asclepius.view.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.AppRepository
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: AppRepository) : ViewModel() {
    val getHistory: LiveData<Results<List<HistoryEntity>>> =
        repository.getHistory()

    fun deleteHistory(history: HistoryEntity) {
        viewModelScope.launch {
            repository.deleteHistory(history)
        }
    }
}