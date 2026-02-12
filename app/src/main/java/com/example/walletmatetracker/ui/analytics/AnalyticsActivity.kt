package com.example.walletmatetracker.ui.analytics


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.databinding.ActivityAnalyticsBinding
import kotlinx.coroutines.launch

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding

    private val viewModel: AnalyticsViewModel by viewModels {
        val dao = ExpenseDatabase.getDatabase(this).expenseDao()
        AnalyticsViewModelFactory(ExpenseRepository(dao))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = RecommendationAdapter()

        binding.rvRecommendations.layoutManager =
            LinearLayoutManager(this)

        binding.rvRecommendations.adapter = adapter

        lifecycleScope.launch {
            viewModel.recommendations.collect { list ->
                adapter.submitList(list)
            }
        }
    }
}

