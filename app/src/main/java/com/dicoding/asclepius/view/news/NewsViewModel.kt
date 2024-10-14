package com.dicoding.asclepius.view.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.AppRepository
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.data.remote.response.ArticlesItem

class NewsViewModel(repository: AppRepository) : ViewModel() {
    val getNews: LiveData<Results<List<ArticlesItem>>> =
        repository.getNews()
}