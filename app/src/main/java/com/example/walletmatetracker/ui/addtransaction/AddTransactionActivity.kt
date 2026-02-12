package com.example.walletmatetracker.ui.addtransaction

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.widget.ArrayAdapter
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.databinding.ActivityAddTransactionBinding
import com.example.walletmatetracker.domain.model.ExpenseCategory
import com.example.walletmatetracker.domain.model.IncomeCategory
import com.example.walletmatetracker.domain.model.TransactionType
import com.example.walletmatetracker.ui.main.MainViewModel
import com.example.walletmatetracker.ui.main.MainViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    private var transactionType = TransactionType.EXPENSE
    private var selectedDate = System.currentTimeMillis()
    private var receiptUri: String? = null
    private var editId: String? = null

    private val viewModel: MainViewModel by viewModels {
        val dao = ExpenseDatabase.getDatabase(this).expenseDao()
        MainViewModelFactory(ExpenseRepository(dao))
    }

    // Image picker (defined ONCE, outside onCreate)
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {

                // Persist read permission
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                receiptUri = it.toString()
                binding.imgReceiptPreview.setImageURI(it)
                binding.imgReceiptPreview.visibility = View.VISIBLE
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editId = intent.getStringExtra("transaction_id")

        setupToggle()
        setupSpinners()
        setupDatePicker()
        setupImagePicker()
        setupSave()
        loadDataIfEditing()
    }

    // ------------------------------
    // Populate existing data (Edit)
    // ------------------------------
    private fun loadDataIfEditing() {
        editId?.let { id ->
            lifecycleScope.launch {
                val existing = viewModel.getExpenseById(id)
                existing?.let { populateFields(it) }
            }
        }
    }

    private fun populateFields(expense: ExpenseEntity) {

        transactionType = expense.transactionType
        selectedDate = expense.date

        binding.etAmount.setText(expense.amount.toString())
        binding.etDescription.setText(expense.description)

        binding.tvDate.text =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(expense.date))

        // Toggle type properly
        if (expense.transactionType == TransactionType.INCOME) {
            binding.btnIncome.isChecked = true
        } else {
            binding.btnExpense.isChecked = true
        }

        updateCategorySpinner()
    }

    // ------------------------------
    // Toggle
    // ------------------------------
    private fun setupToggle() {
        binding.toggleTransactionType.addOnButtonCheckedListener { _, checkedId, _ ->
            transactionType =
                if (checkedId == binding.btnIncome.id)
                    TransactionType.INCOME
                else
                    TransactionType.EXPENSE

            updateCategorySpinner()
        }
    }

    // ------------------------------
    // Spinners
    // ------------------------------
    private fun setupSpinners() {

        binding.spinnerPayment.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Cash", "UPI", "Card", "Bank")
        )

        binding.spinnerCurrency.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("₹ INR", "$ USD", "€ EUR")
        )

        updateCategorySpinner()
    }

    private fun updateCategorySpinner() {
        val categories =
            if (transactionType == TransactionType.EXPENSE)
                ExpenseCategory.values().map { it.name }
            else
                IncomeCategory.values().map { it.name }

        binding.spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
    }

    // ------------------------------
    // Date Picker
    // ------------------------------
    private fun setupDatePicker() {

        binding.tvDate.setOnClickListener {
            val cal = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    cal.set(year, month, dayOfMonth)
                    selectedDate = cal.timeInMillis

                    binding.tvDate.text =
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(cal.time)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // ------------------------------
    // Image Picker
    // ------------------------------
    private fun setupImagePicker() {
        binding.btnAttachReceipt.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }
    }

    // ------------------------------
    // Save Logic
    // ------------------------------
    private fun setupSave() {

        binding.btnSave.setOnClickListener {

            val amount =
                binding.etAmount.text.toString()
                    .toDoubleOrNull() ?: return@setOnClickListener

            val transaction = ExpenseEntity(
                id = editId ?: UUID.randomUUID().toString(),
                amount = amount,
                category = binding.spinnerCategory.selectedItem.toString(),
                description = binding.etDescription.text.toString(),
                date = selectedDate,
                currency = binding.spinnerCurrency.selectedItem.toString(),
                paymentMode = binding.spinnerPayment.selectedItem.toString(),
                transactionType = transactionType ,
                receiptUri = receiptUri
            )

            lifecycleScope.launch {
                if (editId != null) {
                    viewModel.updateExpense(transaction)
                } else {
                    viewModel.addExpense(transaction)
                }
                finish()
            }
        }
    }
}
