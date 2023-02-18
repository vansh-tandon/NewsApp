package io.project.newsappmvvm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.project.newsappmvvm.repository.NewsRepository

class NewsViewModelProviderFactory(private val newsRepository: NewsRepository) :ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository) as T

    }
}