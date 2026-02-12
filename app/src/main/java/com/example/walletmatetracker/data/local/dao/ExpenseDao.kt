package com.example.walletmatetracker.data.local.dao


import androidx.room.*
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    /* ---------------- BASIC CRUD ---------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    /* ---------------- FETCHING ---------------- */

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    suspend fun getExpenseById(expenseId: String): ExpenseEntity?

    /* ---------------- FILTERING ---------------- */

    @Query("""
        SELECT * FROM expenses 
        WHERE date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC
    """)
    fun getExpensesBetweenDates(
        startDate: Long,
        endDate: Long
    ): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT * FROM expenses 
        WHERE category = :category 
        ORDER BY date DESC
    """)
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>

    /* ---------------- CHART & ANALYTICS ---------------- */

    @Query("""
        SELECT category, SUM(amount) as totalAmount 
        FROM expenses 
        GROUP BY category
    """)
    fun getTotalSpentByCategory(): Flow<List<CategoryTotal>>

    @Query("""
        SELECT SUM(amount) 
        FROM expenses 
        WHERE date BETWEEN :startDate AND :endDate
    """)
    fun getTotalSpentBetweenDates(
        startDate: Long,
        endDate: Long
    ): Flow<Double?>

    @Query("""
        SELECT AVG(amount) 
        FROM expenses
    """)
    fun getAverageExpense(): Flow<Double?>

    /* ---------------- SYNC SUPPORT ---------------- */

    @Query("SELECT * FROM expenses WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncExpenses(): List<ExpenseEntity>

    @Query("""
        UPDATE expenses 
        SET syncStatus = :status, lastModified = :lastModified 
        WHERE id = :expenseId
    """)
    suspend fun updateSyncStatus(
        expenseId: String,
        status: String,
        lastModified: Long
    )
}

/**
 * Used for chart aggregation
 */
data class CategoryTotal(
    val category: String,
    val totalAmount: Double
)
