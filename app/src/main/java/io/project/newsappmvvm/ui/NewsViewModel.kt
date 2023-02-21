package io.project.newsappmvvm.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.newsappmvvm.models.NewsResponse
import io.project.newsappmvvm.repository.NewsRepository
import io.project.newsappmvvm.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Query

//here we have the instance of newsRepository
//from here we will call the function from our newsRepository
//and here we'll also handle responses of our request and then
//we'll have live data obj. to notify all of our fragments about changes
//regarding these request
class NewsViewModel(val newsRepository: NewsRepository): ViewModel() {

    //live data obj.
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    //for pagination
    val  breakingNewsPage = 1

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val  searchNewsPage = 1

    init {
        getBreakingNews("in")
    }

    //fun that executes API call from the repository
    //viewModelScope will make sure that this coroutine stays alive as long as
    //our view model is alive
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}