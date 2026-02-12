package com.example.walletmatetracker.data.repository


import com.example.walletmatetracker.data.local.dao.ExpenseDao
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.data.local.dao.CategoryTotal
import com.example.walletmatetracker.domain.model.TransactionType

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) {

    /* ---------------- CRUD ---------------- */

    suspend fun addExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    /* ---------------- READ ---------------- */

    fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }

    suspend fun getExpenseById(id: String): ExpenseEntity? {
        return expenseDao.getExpenseById(id)
    }

    /* ---------------- FILTER ---------------- */

    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategory(category)
    }

    fun getExpensesBetweenDates(
        startDate: Long,
        endDate: Long
    ): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }

    /* ---------------- ANALYTICS ---------------- */

    fun getCategoryWiseSpending(): Flow<List<CategoryTotal>> {
        return expenseDao.getTotalSpentByCategory()
    }

    fun getTotalSpentBetweenDates(
        startDate: Long,
        endDate: Long
    ): Flow<Double?> {
        return expenseDao.getTotalSpentBetweenDates(startDate, endDate)
    }

    fun getAverageExpense(): Flow<Double?> {
        return expenseDao.getAverageExpense()
    }

    /* ---------------- SYNC SUPPORT ---------------- */

    suspend fun getPendingSyncExpenses(): List<ExpenseEntity> {
        return expenseDao.getPendingSyncExpenses()
    }

    suspend fun updateSyncStatus(
        expenseId: String,
        status: String,
        lastModified: Long
    ) {
        expenseDao.updateSyncStatus(expenseId, status, lastModified)
    }

    fun getTransactionsByType(type: TransactionType) =
        expenseDao.getAllExpenses() // filtered later in ViewModel/usecase

}
