package io.project.newsappmvvm.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.project.newsappmvvm.repository.NewsRepository

class NewsViewModelProviderFactory(private val newsRepository: NewsRepository,
val application: Application) :ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, application) as T

    }
}