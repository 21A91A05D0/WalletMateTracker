package com.example.walletmatetracker.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.walletmatetracker.R
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.databinding.ItemExpenseBinding
import com.example.walletmatetracker.domain.model.TransactionType
import com.example.walletmatetracker.ui.util.CategoryIconMapper
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private val onItemClick: (ExpenseEntity) -> Unit
) : ListAdapter<ExpenseEntity, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ExpenseEntity>() {
            override fun areItemsTheSame(
                oldItem: ExpenseEntity,
                newItem: ExpenseEntity
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ExpenseEntity,
                newItem: ExpenseEntity
            ): Boolean = oldItem == newItem
        }
    }

    inner class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: ExpenseEntity) {
            binding.tvCategory.text = expense.category
            binding.tvDescription.text = expense.description


            // Set icon
            binding.imgCategory.setImageResource(
                CategoryIconMapper.getIcon(expense.category)
            )

            // Set amount text
            val formattedAmount = "₹${expense.amount}"

            if (expense.transactionType == TransactionType.EXPENSE) {
                binding.tvAmount.text = "-$formattedAmount"
                binding.tvAmount.setTextColor(
                    binding.root.context.getColor(R.color.red_expense)
                )
            } else {
                binding.tvAmount.text = "+$formattedAmount"
                binding.tvAmount.setTextColor(
                    binding.root.context.getColor(R.color.green_income)
                )
            }

            binding.tvDate.text = formatDate(expense.date)

            binding.root.setOnClickListener {
                onItemClick(expense)
            }

        }



        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
