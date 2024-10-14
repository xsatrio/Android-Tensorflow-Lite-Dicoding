package com.dicoding.asclepius.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.local.HistoryDao
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.remote.retrofit.ApiService

class AppRepository(
    private val historyDao: HistoryDao,
    private val apiService: ApiService
) {

    fun getHistory(): LiveData<Results<List<HistoryEntity>>> = liveData {
        emit(Results.Loading)
        try {
            val localData: LiveData<Results<List<HistoryEntity>>> =
                historyDao.getHistory().map { Results.Success(it) }
            emitSource(localData)
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    suspend fun isHistoryExists(imageUri: String): Boolean {
        return historyDao.checkHistoryExists(imageUri) != null
    }

    suspend fun insertHistory(history: List<HistoryEntity>) {
        historyDao.insertHistory(history)
    }

    suspend fun deleteHistory(history: HistoryEntity) {
        historyDao.deleteHistory(history)
    }

    fun getNews(): LiveData<Results<List<ArticlesItem>>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getNews(
                query = "cancer",
                category = "health",
                language = "en",
                apiKey = BuildConfig.API_KEY
            )
            val filteredNews =
                response.articles.filter { it.author != null && it.urlToImage != null }
            emit(Results.Success(filteredNews))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(
            apiService: ApiService,
            historyDao: HistoryDao
        ): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(historyDao, apiService)
            }.also { instance = it }
    }

}