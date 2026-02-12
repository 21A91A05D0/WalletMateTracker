package com.example.walletmatetracker.ui.addexpense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.databinding.FragmentAddExpenseBinding
import com.example.walletmatetracker.ui.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddExpenseBottomSheet(
    private val viewModel: MainViewModel
) : BottomSheetDialogFragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val amountText = binding.etAmount.text.toString()
        val category = binding.etCategory.text.toString()
        val description = binding.etDescription.text.toString()

        if (amountText.isBlank() || category.isBlank()) {
            Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = ExpenseEntity(
            amount = amountText.toDouble(),
            category = category,
            description = description,
            date = System.currentTimeMillis(),
            currency = "₹",
            paymentMode = "UPI"
        )

        viewModel.addExpense(expense)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
