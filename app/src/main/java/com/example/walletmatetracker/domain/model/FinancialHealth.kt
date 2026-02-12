package com.example.walletmatetracker.domain.model



data class FinancialHealth(
    val score: Int,
    val status: String,
    val income: Double,
    val expense: Double,
    val savings: Double
)
