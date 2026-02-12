package com.example.walletmatetracker.ui.charts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletmatetracker.data.local.entity.ExpenseEntity

import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import com.example.walletmatetracker.ui.charts.CategoryTotal


class ChartsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    // -----------------------------
    // Filter State (Month, Year, Period)
    // -----------------------------
    private val filterState = MutableStateFlow(
        Quadruple(
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.YEAR),
            TimeFilterType.MONTH,
            Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        )
    )


    fun updateFilter(
        month: Int,
        year: Int,
        type: TimeFilterType,
        week: Int = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
    ) {
        filterState.value = Quadruple(month, year, type, week)
    }


    // -----------------------------
    // Time Filtered Bar Chart Data
    // -----------------------------
    val timeFilteredData: StateFlow<Map<String, Double>> =
        combine(
            repository.getAllExpenses(),
            filterState
        ) { transactions, filter ->

            //val (month, year, type) = filter
            val (month, year, type, selectedWeek) = filter

            val calendar = Calendar.getInstance()

            // -----------------------------
            // STEP 1: Filter transactions
            // -----------------------------
            val filteredExpenses = transactions.filter {

                calendar.timeInMillis = it.date

                val matchesPeriod = when (type) {

                    TimeFilterType.MONTH ->
                        calendar.get(Calendar.MONTH) == month &&
                                calendar.get(Calendar.YEAR) == year

                    TimeFilterType.YEAR ->
                        calendar.get(Calendar.YEAR) == year

                    TimeFilterType.WEEK -> {
                        val currentWeek =
                            Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)

                        calendar.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                calendar.get(Calendar.YEAR) == year
                    }
                }

                matchesPeriod &&
                        it.transactionType == TransactionType.EXPENSE
            }

            // -----------------------------
            // STEP 2: Group Based On Period
            // -----------------------------
            when (type) {

                TimeFilterType.MONTH -> {
                    filteredExpenses
                        .groupBy {
                            calendar.timeInMillis = it.date
                            "Week ${calendar.get(Calendar.WEEK_OF_MONTH)}"
                        }
                        .mapValues { entry ->
                            entry.value.sumOf { it.amount }
                        }
                }

                TimeFilterType.WEEK -> {
                    filteredExpenses
                        .groupBy {
                            calendar.timeInMillis = it.date
                            SimpleDateFormat("EEE", Locale.getDefault())
                                .format(Date(it.date))
                        }
                        .mapValues { entry ->
                            entry.value.sumOf { it.amount }
                        }
                }

                TimeFilterType.YEAR -> {
                    filteredExpenses
                        .groupBy {
                            calendar.timeInMillis = it.date
                            SimpleDateFormat("MMM", Locale.getDefault())
                                .format(Date(it.date))
                        }
                        .mapValues { entry ->
                            entry.value.sumOf { it.amount }
                        }
                }
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )




    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<ChartUiState> =
        combine(
            repository.getAllExpenses(),
            filterState
        ) { transactions, filter ->

            val (month, year, type, selectedWeek) = filter
            val calendar = Calendar.getInstance()

            // ----------------------------
            // Period Filtering
            // ----------------------------
            val filteredExpenses = transactions.filter { expense ->

                calendar.timeInMillis = expense.date

                val matchesPeriod = when (type) {

                    TimeFilterType.MONTH ->
                        calendar.get(Calendar.MONTH) == month &&
                                calendar.get(Calendar.YEAR) == year

                    TimeFilterType.YEAR ->
                        calendar.get(Calendar.YEAR) == year

                    TimeFilterType.WEEK ->
                        calendar.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                calendar.get(Calendar.YEAR) == year
                }

                matchesPeriod &&
                        expense.transactionType == TransactionType.EXPENSE
            }

            // ----------------------------
            // Pie Chart Category Totals
            // ----------------------------
            val categoryTotals =
                filteredExpenses
                    .groupBy { it.category }
                    .map { entry ->
                        CategoryTotal(
                            category = entry.key,
                            totalAmount = entry.value.sumOf { it.amount }
                        )
                    }

            // ----------------------------
            // Statistics
            // ----------------------------
            val highestCategory =
                categoryTotals.maxByOrNull { it.totalAmount }?.category ?: "-"

            val totalExpense =
                filteredExpenses.sumOf { it.amount }

            val totalIncome =
                transactions
                    .filter { it.transactionType == TransactionType.INCOME }
                    .sumOf { it.amount }

            val averageDaily =
                if (filteredExpenses.isNotEmpty())
                    totalExpense /
                            maxOf(filteredExpenses.size, 1)
                else 0.0

            ChartUiState(
                categoryTotals = categoryTotals,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                highestCategory = highestCategory,
                averageDailyExpense = averageDaily
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ChartUiState()
        )

}

