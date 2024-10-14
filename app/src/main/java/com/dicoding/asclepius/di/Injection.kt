package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.AppRepository
import com.dicoding.asclepius.data.local.HistoryDatabase
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig

object Injection {
    fun provideHistoryRepository(context: Context): AppRepository {
        val apiService = ApiConfig.getApiService()
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        return AppRepository.getInstance(apiService, dao)
    }
}