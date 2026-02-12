package com.example.walletmatetracker.ui.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    /* ---------------- UI STATE ---------------- */

    val expenses: StateFlow<List<ExpenseEntity>> =
        repository.getAllExpenses()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val categoryWiseSpending =
        repository.getCategoryWiseSpending()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val averageExpense =
        repository.getAverageExpense()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

    /* ---------------- ACTIONS ---------------- */

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }

    suspend fun getExpenseById(id: String): ExpenseEntity? {
        return repository.getExpenseById(id)
    }


    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun getExpensesBetweenDates(
        startDate: Long,
        endDate: Long
    ): StateFlow<List<ExpenseEntity>> {
        return repository.getExpensesBetweenDates(startDate, endDate)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }
}
