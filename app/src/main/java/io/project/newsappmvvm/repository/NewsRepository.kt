package io.project.newsappmvvm.repository

import io.project.newsappmvvm.api.RetrofitInstance
import io.project.newsappmvvm.db.ArticleDatabase
import io.project.newsappmvvm.models.Article
import retrofit2.Retrofit


//purpose of newsRepository is to get data from the database
//and from our remote data source i.e from retrofit from our api
//here we'll have a func. that directly queries our API for the news
//and in the NewsViewModel we have the instance of newsRepository
class NewsRepository(val db: ArticleDatabase) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    //For managing the ArticleDao
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    //fun. to return live data
    fun getSavedNews() = db.getArticleDao().getAllArticles()

    //fun. to delete an article
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticles(article)


}