package io.project.newsappmvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import io.project.newsappmvvm.R
import io.project.newsappmvvm.databinding.ActivityNewsBinding

//import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.bottomNavigationView.setupWithNavController(binding.newsNavHostFragment.findNavController())
    }
}
