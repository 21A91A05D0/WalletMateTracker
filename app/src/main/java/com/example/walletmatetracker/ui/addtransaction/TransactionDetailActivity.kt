package com.example.walletmatetracker.ui.addtransaction

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.databinding.ActivityTransactionDetailBinding
import com.example.walletmatetracker.domain.model.TransactionType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionDetailBinding
    private lateinit var repository: ExpenseRepository

    private var transaction: ExpenseEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = ExpenseDatabase.getDatabase(this).expenseDao()
        repository = ExpenseRepository(dao)

        val id = intent.getStringExtra("transaction_id") ?: return

        lifecycleScope.launch {
            transaction = repository.getExpenseById(id)
            transaction?.let { bindData(it) }
        }

        setupActions()
    }

    private fun bindData(expense: ExpenseEntity) {

        val amountText = if (expense.transactionType == TransactionType.EXPENSE)
            "-₹${expense.amount}"
        else
            "+₹${expense.amount}"

        binding.tvAmount.text = amountText
        binding.tvCategory.text = expense.category
        binding.tvDescription.text = expense.description
        binding.tvPayment.text = expense.paymentMode
        binding.tvCurrency.text = expense.currency

        val formattedDate = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(expense.date))

        binding.tvDate.text = formattedDate

        expense.receiptUri?.let {
            try {
                binding.imgReceipt.setImageURI(Uri.parse(it))
                binding.imgReceipt.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.imgReceipt.visibility = View.GONE
            }
        }

    }

    private fun setupActions() {

        binding.btnDelete.setOnClickListener {

            MaterialAlertDialogBuilder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    transaction?.let {
                        lifecycleScope.launch {
                            repository.deleteExpense(it)
                            finish()
                        }
                    }
                }
                .show()
        }


        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("transaction_id", transaction?.id)
            startActivity(intent)
            finish()
        }
    }
}
