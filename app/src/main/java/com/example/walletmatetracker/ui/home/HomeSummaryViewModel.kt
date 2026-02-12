package com.example.walletmatetracker.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeSummaryViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val summaryState: StateFlow<HomeSummaryUiState> =
        repository.getAllExpenses()
            .map { transactions ->
                val income = transactions
                    .filter { it.transactionType == TransactionType.INCOME }
                    .sumOf { it.amount }

                val expense = transactions
                    .filter { it.transactionType == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                HomeSummaryUiState(
                    totalIncome = income,
                    totalExpense = expense,
                    balance = income - expense
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                HomeSummaryUiState()
            )
}
