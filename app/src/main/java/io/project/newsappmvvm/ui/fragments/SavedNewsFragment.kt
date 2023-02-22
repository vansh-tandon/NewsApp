package io.project.newsappmvvm.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.project.newsappmvvm.R
import io.project.newsappmvvm.adapters.NewsAdapter
import io.project.newsappmvvm.databinding.FragmentSavedNewsBinding
import io.project.newsappmvvm.databinding.FragmentSearchNewsBinding
import io.project.newsappmvvm.ui.NewsActivity
import io.project.newsappmvvm.ui.NewsViewModel


class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentSavedNewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSavedNewsBinding.bind(view)

        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()

        newsAdapter.setOnClickListener {
            val bundle = Bundle().apply {

                putSerializable("article", it)
            }

            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment, bundle)
        }

    }
    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}