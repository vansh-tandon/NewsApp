package io.project.newsappmvvm.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.newsappmvvm.NewsApplication
import io.project.newsappmvvm.models.Article
import io.project.newsappmvvm.models.NewsResponse
import io.project.newsappmvvm.repository.NewsRepository
import io.project.newsappmvvm.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

//For pagination first of all we'll have to save current response in
//NewsViewModel, we'll do it here coz it doesn't destroy on device rotation


//here we have the instance of newsRepository
//from here we will call the function from our newsRepository
//and here we'll also handle responses of our request and then
//we'll have live data obj. to notify all of our fragments about changes
//regarding these request
class NewsViewModel(
    val newsRepository: NewsRepository,
    application: Application): AndroidViewModel(application) {

    //live data obj.
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    //for pagination
    var  breakingNewsPage = 1

    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null


    init {
        getBreakingNews("in")
    }

    //fun that executes API call from the repository
    //viewModelScope will make sure that this coroutine stays alive as long as
    //our view model is alive
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
    safeBreakingNewsCall(countryCode)
    //        breakingNews.postValue(Resource.Loading())
//        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
//        breakingNews.postValue(handleBreakingNewsResponse(response))
        //shifted to saveBreakingNewsCall
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    //        searchNews.postValue(Resource.Loading())
//        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
//        searchNews.postValue(handleSearchNewsResponse(response))
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

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    //to check if we are connected to internet
    //for that we need connectivity manager i.e a sys. service that requires a context,
    //we can't simply call this inside of a view model, but we need the context for this, and
    //we can't implement this func in activity class coz we need it inside a view model class
    //for that it would be a bad practice to pass activity context to newsViewModel constructor
    //coz it violate the purpose of having a viewModel i.e to separate activity data from the ui
    //if we'll use that then if the activity gets destroyed then you cannot simply use that context anymore
    //we can use application context as it lives as long as our whole application, for that instead of
    //view inheriting newsViewModel from ViewModel we will inherit it from Android View Model(same as ViewModel)
    //but in avm we can use application context

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
//    private fun hasInternetConnection(): Boolean{
//        //this function getApplication  is only available in Android View Model
//        //not in normal View Model
//        val connectivityManager  = getApplication<NewsApplication>()
//            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //M i.e api 26
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            val activeNetwork = connectivityManager.activeNetwork ?: return false
//            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: false
//            return when {
//                capabilities.hasTranport(TRANSPORT_WIFI) -> true
//            }
//        }

//    }

}