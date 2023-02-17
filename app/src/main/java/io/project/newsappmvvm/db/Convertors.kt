package io.project.newsappmvvm.db

import androidx.room.TypeConverter
import io.project.newsappmvvm.models.Source

class Convertors {


    //here we r just simply telling that whenever we get source
    //just convert it to string, by just taking the name of the source
    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name
    }

    //and vice versa
    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}