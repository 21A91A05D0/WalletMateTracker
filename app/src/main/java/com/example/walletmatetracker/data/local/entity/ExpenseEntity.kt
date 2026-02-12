package com.example.walletmatetracker.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import com.example.walletmatetracker.domain.model.TransactionType


@Entity(tableName = "expenses")
data class ExpenseEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val amount: Double,

    val category: String, // Food, Transport, Bills, etc.

    val description: String,

    val date: Long,
    // Stored as epoch millis for:
    // - sorting
    // - filtering
    // - charts (weekly/monthly)

    val currency: String, // INR, USD, EUR

    val paymentMode: String,
    // Cash, Card, UPI, Bank

    val isRecurring: Boolean = false,

    val note: String? = null,

    val createdAt: Long = System.currentTimeMillis(),

    val lastModified: Long = System.currentTimeMillis(),

    val syncStatus: SyncStatus = SyncStatus.LOCAL,

    val transactionType: TransactionType = TransactionType.EXPENSE,

    val receiptUri: String? = null


)

/**
 * Sync state for Firebase
 */
enum class SyncStatus {
    LOCAL,
    SYNCED,
    CONFLICT
}
