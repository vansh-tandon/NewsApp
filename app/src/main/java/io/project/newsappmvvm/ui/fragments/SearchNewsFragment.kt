package io.project.newsappmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.project.newsappmvvm.R
import io.project.newsappmvvm.adapters.NewsAdapter
import io.project.newsappmvvm.databinding.FragmentSearchNewsBinding
import io.project.newsappmvvm.ui.NewsActivity
import io.project.newsappmvvm.ui.NewsViewModel
import io.project.newsappmvvm.util.Constants
import io.project.newsappmvvm.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import io.project.newsappmvvm.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentSearchNewsBinding
    lateinit var newsAdapter: NewsAdapter
    val TAG = "SearchNewsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchNewsBinding.bind(view)

        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnClickListener {
            val bundle = Bundle().apply {

                putSerializable("article", it)
            }

            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment, bundle)
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener {editable ->
            //to cancel our current job i.e during the delay time,
            // anything else wasn't passes
            job?.cancel()
            //then to start new job
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                     if(editable.toString().isNotEmpty()){
                         viewModel.searchNews(editable.toString())
                     }
                }
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if(isLastPage){
                            binding.rvSearchNews.setPadding(0,0,0,0)
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
//                        Log.e(TAG, "An error occurred: $message")
                        Toast.makeText(activity, "An error occurred: $message",
                            Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })
    }
    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    //pagination
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    //assigning a anonymous object to variable i.e object expression,
    //we used object as an expression
    //there will be only one instance of it
    //i.e like anonymous obj. we can create properties and method for it
    //we can also use anonymous obj to implement a interface/class

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //to check if we are currently scrolling
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //to find whether we have scrolled to the bottom or not
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            //with these three nos we'll calculate if we have scrolled to the bottom
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            //to show that our last item is visible
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            //to check if we have as many items in our rv than our query page size
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem &&
                    isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                viewModel.searchNews(binding.etSearch.text.toString())
            }
            else{
                binding.rvSearchNews.setPadding(0,0,0,0)
            }
        }
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}