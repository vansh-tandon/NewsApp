package io.project.newsappmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.project.newsappmvvm.R
import io.project.newsappmvvm.adapters.NewsAdapter
import io.project.newsappmvvm.databinding.FragmentBreakingNewsBinding
import io.project.newsappmvvm.ui.NewsActivity
import io.project.newsappmvvm.ui.NewsViewModel
import io.project.newsappmvvm.util.Resource

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentBreakingNewsBinding

    private val TAG = "Breaking News Fragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBreakingNewsBinding.bind(view)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
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
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}