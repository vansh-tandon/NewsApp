package io.project.newsappmvvm.ui

import androidx.lifecycle.ViewModel
import io.project.newsappmvvm.repository.NewsRepository

class NewsViewModel(val newsRepository: NewsRepository): ViewModel() {
}