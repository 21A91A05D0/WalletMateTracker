package com.example.walletmatetracker.domain.usecase



import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.domain.model.TransactionType

object FinanceCalculator {

    fun calculateTotalIncome(transactions: List<ExpenseEntity>): Double {
        return transactions
            .filter { it.transactionType == TransactionType.INCOME }
            .sumOf { it.amount }
    }

    fun calculateTotalExpense(transactions: List<ExpenseEntity>): Double {
        return transactions
            .filter { it.transactionType == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    fun calculateBalance(transactions: List<ExpenseEntity>): Double {
        return calculateTotalIncome(transactions) -
                calculateTotalExpense(transactions)
    }
}
