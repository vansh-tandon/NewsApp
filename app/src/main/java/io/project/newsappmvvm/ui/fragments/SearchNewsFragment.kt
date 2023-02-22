package io.project.newsappmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.project.newsappmvvm.R
import io.project.newsappmvvm.adapters.NewsAdapter
import io.project.newsappmvvm.databinding.FragmentSearchNewsBinding
import io.project.newsappmvvm.ui.NewsActivity
import io.project.newsappmvvm.ui.NewsViewModel
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
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
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
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}