package io.project.newsappmvvm.db

import android.content.Context
import androidx.room.*
import io.project.newsappmvvm.models.Article

//database class for room always need to be abstract
@Database (
    entities =[Article::class],
    version = 2
)
@TypeConverters(Convertors::class)
abstract class ArticleDatabase : RoomDatabase() {
    //this is an abstract function so we don't need to implement it
    //the implementation of it will happen behind the scene, room will do that for us
    abstract fun getArticleDao(): ArticleDAO

    //while calling the constructor invoke function is also called, for eg. given below
//    ArticleDatabase()
    //i.e whenever we initialise or instantiate the object invoke fun will we called

    //Companion object to create actual database
    companion object{
        //i.e other threats can immediately see when a thread changes this instance
        @Volatile
        //instance of article database
        private var instance: ArticleDatabase? = null
        //to synchronise setting that instance
        //to make sure that there is really only one instance of our database at once
        private val LOCK = Any()

        //this is called whenever we create an instance of our database
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            //i.e any thing that happens inside this block of code, can be accessed by the other
            //threads at the same time
            //so here we'll make sure that there is no other thread that sets this instance
            //to something while we already set it
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).fallbackToDestructiveMigration()
                .build()
    }

}