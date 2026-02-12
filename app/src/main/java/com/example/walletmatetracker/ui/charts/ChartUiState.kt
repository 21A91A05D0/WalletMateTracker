package com.example.walletmatetracker.ui.charts

data class ChartUiState(
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val highestCategory: String = "-",
    val averageDailyExpense: Double = 0.0
)
