package com.example.walletmatetracker.domain.usecase

import com.example.walletmatetracker.domain.model.ExpenseCategory
import com.example.walletmatetracker.domain.model.IncomeCategory
import com.example.walletmatetracker.domain.model.TransactionType

object TransactionCategoryMapper {

    fun isValidCategory(
        transactionType: TransactionType,
        category: String
    ): Boolean {
        return when (transactionType) {
            TransactionType.EXPENSE ->
                ExpenseCategory.values().any { it.name == category }

            TransactionType.INCOME ->
                IncomeCategory.values().any { it.name == category }
        }
    }

    fun getDefaultCategory(transactionType: TransactionType): String {
        return when (transactionType) {
            TransactionType.EXPENSE -> ExpenseCategory.OTHER.name
            TransactionType.INCOME -> IncomeCategory.OTHER.name
        }
    }
}
