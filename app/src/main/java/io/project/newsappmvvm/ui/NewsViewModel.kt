package io.project.newsappmvvm.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.newsappmvvm.models.Article
import io.project.newsappmvvm.models.NewsResponse
import io.project.newsappmvvm.repository.NewsRepository
import io.project.newsappmvvm.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

//For pagination first of all we'll have to save current response in
//NewsViewModel, we'll do it here coz it doesn't destroy on device rotation


//here we have the instance of newsRepository
//from here we will call the function from our newsRepository
//and here we'll also handle responses of our request and then
//we'll have live data obj. to notify all of our fragments about changes
//regarding these request
class NewsViewModel(val newsRepository: NewsRepository): ViewModel() {

    //live data obj.
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    //for pagination
    var  breakingNewsPage = 1

    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null


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
                //every time we get response we increase the pg no. by 1
                breakingNewsPage++
                //if that response is the first response, bnr = rr, with all available articles
                //and all the pages that we loaded yet
                if (breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }
                else{
                    //if we have loaded more than 1 page already
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                //every time we get response we increase the pg no. by 1
                searchNewsPage++
                //if that response is the first response, snr = rr, with all available articles
                //and all the pages that we loaded yet
                if (searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }
                else{
                    //if we have loaded more than 1 page already
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    //next step is to detect when we completely scroll down then we want to paginate
    //our request and we want to load the nxt page, that we need to do in breaking news frag,
    //and search news fragment because these are the two frags that are able to
    //paginate our request


    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }
}