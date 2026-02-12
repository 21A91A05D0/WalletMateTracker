package com.example.walletmatetracker.ui.analytics



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.domain.BudgetAnalyzer
import com.example.walletmatetracker.domain.model.Recommendation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val recommendations: StateFlow<List<Recommendation>> =
        repository.getAllExpenses()
            .map { transactions ->
                BudgetAnalyzer.analyze(transactions)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}
