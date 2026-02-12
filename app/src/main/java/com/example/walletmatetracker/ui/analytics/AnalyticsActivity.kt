package com.example.walletmatetracker.ui.analytics


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walletmatetracker.R
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
            viewModel.uiState.collect { state ->

                state.health?.let { health ->

                    binding.tvScore.text = health.score.toString()
                    binding.tvStatus.text = health.status

                    binding.progressHealth.progress = health.score

                    binding.tvInsight.text = when (health.status) {
                        "Good" -> "You're managing your finances well. Keep it up!"
                        "Moderate" -> "You're doing okay. Watch your spending."
                        "Risk" -> "Your expenses are high. Consider reducing costs."
                        else -> "Add income entries to unlock full insights."
                    }

                    updateCardColor(health.status)
                }


                adapter.submitList(state.recommendations)
            }
        }
    }

    private fun updateCardColor(status: String) {

        val color = when (status) {
            "Good" -> "#2ECC71"
            "Moderate" -> "#F1C40F"
            "Risk" -> "#E74C3C"
            else -> "#6C5CE7"
        }

        binding.cardHealth.setCardBackgroundColor(
            android.graphics.Color.parseColor(color)
        )
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }

}
