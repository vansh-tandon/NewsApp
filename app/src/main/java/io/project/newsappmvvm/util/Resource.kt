package io.project.newsappmvvm.util

//used to wrap network responses
//sealed class -> it is just like abstract class but here we can define which classes
//are allowed to inherit from resource class
//here we will define 3 different classes and only those are allowed to inherit
//from resource
//generic type will be used to pass NewsResponse
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String?, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}