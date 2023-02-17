package io.project.newsappmvvm.db

import androidx.lifecycle.LiveData
import androidx.room.*
import io.project.newsappmvvm.models.Article

@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //upsert -> update or insert
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    //now this function wont be a suspend function because
    //it will return a live data object
    //and that doesnt work with suspend function
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticles(article: Article)
}