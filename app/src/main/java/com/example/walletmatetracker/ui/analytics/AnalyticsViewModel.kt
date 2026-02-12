package com.example.walletmatetracker.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.domain.BudgetAnalyzer
import com.example.walletmatetracker.domain.model.FinancialHealth
import com.example.walletmatetracker.domain.model.Recommendation
import kotlinx.coroutines.flow.*

data class AnalyticsUiState(
    val health: FinancialHealth? = null,
    val recommendations: List<Recommendation> = emptyList()
)

class AnalyticsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> =
        repository.getAllExpenses()
            .map { transactions ->

                val health =
                    BudgetAnalyzer.calculateHealth(transactions)

                AnalyticsUiState(
                    health = health,
                    recommendations =
                        BudgetAnalyzer.generateRecommendations(
                            transactions,
                            health
                        )
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AnalyticsUiState()
            )
}
