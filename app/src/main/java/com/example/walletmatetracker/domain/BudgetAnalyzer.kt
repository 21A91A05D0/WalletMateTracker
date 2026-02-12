package com.example.walletmatetracker.domain


import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.domain.model.Recommendation
import com.example.walletmatetracker.domain.model.TransactionType
import java.util.Calendar

object BudgetAnalyzer {

    fun analyze(transactions: List<ExpenseEntity>): List<Recommendation> {

        val recommendations = mutableListOf<Recommendation>()

        val expenses = transactions.filter {
            it.transactionType == TransactionType.EXPENSE
        }

        val incomes = transactions.filter {
            it.transactionType == TransactionType.INCOME
        }

        val totalExpense = expenses.sumOf { it.amount }
        val totalIncome = incomes.sumOf { it.amount }

        if (totalIncome == 0.0) return emptyList()

        val savings = totalIncome - totalExpense

        // 🔹 Overspending Detection
        if (totalExpense > totalIncome * 0.8) {
            recommendations.add(
                Recommendation(
                    title = "⚠ Overspending Alert",
                    description = "You're spending more than 80% of your income.",
                    priority = 3
                )
            )
        }

        // 🔹 Highest Spending Category
        val categoryMap = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val highestCategory = categoryMap.maxByOrNull { it.value }

        highestCategory?.let {

            val percent =
                (it.value / totalIncome) * 100

            if (percent > 30) {

                val reduceAmount = it.value * 0.15

                recommendations.add(
                    Recommendation(
                        title = "💡 Reduce ${it.key}",
                        description = "Reduce ${it.key} spending by ₹${reduceAmount.toInt()} per month.",
                        priority = 2
                    )
                )
            }
        }

        // 🔹 Savings Goal Suggestion
        val suggestedSavings = totalIncome * 0.2

        recommendations.add(
            Recommendation(
                title = "🎯 Monthly Savings Target",
                description = "Try to save ₹${suggestedSavings.toInt()} this month.",
                priority = 1
            )



        )

        return recommendations.sortedByDescending { it.priority }
    }
}
