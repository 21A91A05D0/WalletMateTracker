package com.example.walletmatetracker.ui.addtransaction


//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import com.example.walletmatetracker.data.local.entity.ExpenseEntity
//import com.example.walletmatetracker.databinding.FragmentAddTransactionBinding
//import com.example.walletmatetracker.domain.model.TransactionType
//import com.example.walletmatetracker.domain.model.ExpenseCategory
//import com.example.walletmatetracker.domain.model.IncomeCategory
//import com.example.walletmatetracker.ui.main.MainViewModel
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import java.util.*
//
//class AddTransactionBottomSheet(
//    private val viewModel: MainViewModel
//) : BottomSheetDialogFragment() {
//
//    private var _binding: FragmentAddTransactionBinding? = null
//    private val binding get() = _binding!!
//
//    private var transactionType = TransactionType.EXPENSE
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        setupToggle()
//        setupSpinners()
//        setupSave()
//    }
//
//    private fun setupToggle() {
//        binding.toggleTransactionType.addOnButtonCheckedListener { _, checkedId, _ ->
//            transactionType = if (checkedId == binding.btnIncome.id)
//                TransactionType.INCOME else TransactionType.EXPENSE
//
//            setupCategorySpinner()
//        }
//    }
//
//    private fun setupSpinners() {
//        setupCategorySpinner()
//        binding.spinnerPayment.adapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            listOf("Cash", "UPI", "Card", "Bank")
//        )
//    }
//
//    private fun setupCategorySpinner() {
//        val categories = if (transactionType == TransactionType.EXPENSE)
//            ExpenseCategory.values().map { it.name }
//        else
//            IncomeCategory.values().map { it.name }
//
//        binding.spinnerCategory.adapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            categories
//        )
//    }
//
//    private fun setupSave() {
//        binding.btnSaveTransaction.setOnClickListener {
//            val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: return@setOnClickListener
//
//            val expense = ExpenseEntity(
//                amount = amount,
//                category = binding.spinnerCategory.selectedItem.toString(),
//                description = "",
//                date = System.currentTimeMillis(),
//                currency = "₹",
//                paymentMode = binding.spinnerPayment.selectedItem.toString(),
//                transactionType = transactionType
//            )
//
//            viewModel.addExpense(expense)
//            dismiss()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
