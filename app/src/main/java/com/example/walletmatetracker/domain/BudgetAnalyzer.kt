package com.example.walletmatetracker.domain

import com.example.walletmatetracker.data.local.entity.ExpenseEntity
//import com.example.walletmatetracker.data.local.entity.TransactionType
import com.example.walletmatetracker.domain.model.FinancialHealth
import com.example.walletmatetracker.domain.model.Recommendation
import com.example.walletmatetracker.domain.model.TransactionType


object BudgetAnalyzer {

    fun calculateHealth(transactions: List<ExpenseEntity>): FinancialHealth {

        val expenses =
            transactions.filter { it.transactionType == TransactionType.EXPENSE }

        val incomes =
            transactions.filter { it.transactionType == TransactionType.INCOME }

        val totalExpense = expenses.sumOf { it.amount }
        val totalIncome = incomes.sumOf { it.amount }
        val savings = totalIncome - totalExpense

        if (totalIncome == 0.0) {
            return FinancialHealth(0, "No Income", 0.0, totalExpense, savings)
        }

        val ratio = totalExpense / totalIncome
        val score = ((1 - ratio) * 100).toInt().coerceIn(0, 100)

        val status = when {
            score >= 60 -> "Good"
            score >= 30 -> "Moderate"
            else -> "Risk"
        }

        return FinancialHealth(score, status, totalIncome, totalExpense, savings)
    }

    fun generateRecommendations(
        transactions: List<ExpenseEntity>,
        health: FinancialHealth
    ): List<Recommendation> {

        val recommendations = mutableListOf<Recommendation>()

        // 🔹 Financial Summary (now inside list)
        recommendations.add(
            Recommendation(
                title = "💰 Financial Summary",
                description =
                    "💵 Income: ₹${health.income.toInt()}\n" +
                            "💸 Expense: ₹${health.expense.toInt()}\n" +
                            "💎 Savings: ₹${health.savings.toInt()}"
            )
        )


        val expenses =
            transactions.filter { it.transactionType == TransactionType.EXPENSE }

        val categoryTotals =
            expenses.groupBy { it.category }
                .mapValues { it.value.sumOf { tx -> tx.amount } }

        categoryTotals.maxByOrNull { it.value }?.let {
            recommendations.add(
                Recommendation(
                    title = "📊 Highest Spending Category",
                    description =
                        "${it.key} costs you ₹${it.value.toInt()} this period."
                )
            )
        }

        if (health.score < 60 && health.income > 0) {
            val suggestedSavings = health.income * 0.2
            recommendations.add(
                Recommendation(
                    title = "🎯 Savings Suggestion",
                    description =
                        "Try saving ₹${suggestedSavings.toInt()} this month."
                )
            )
        }

        return recommendations
    }
}
