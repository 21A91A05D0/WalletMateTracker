package com.example.walletmatetracker.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walletmatetracker.R
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

import com.example.walletmatetracker.ui.addtransaction.AddTransactionActivity
import com.example.walletmatetracker.ui.addtransaction.TransactionDetailActivity
import com.example.walletmatetracker.ui.home.HomeSummaryViewModel

import com.example.walletmatetracker.ui.charts.ChartsActivity


//import com.example.walletmatetracker.ui.addtransaction.AddTransactionBottomSheet


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var expenseAdapter: ExpenseAdapter


    private val viewModel: MainViewModel by viewModels {
        val dao = ExpenseDatabase
            .getDatabase(applicationContext)
            .expenseDao()
        MainViewModelFactory(ExpenseRepository(dao))
    }

    private val summaryViewModel: HomeSummaryViewModel by viewModels {
        val dao = ExpenseDatabase.getDatabase(applicationContext).expenseDao()
        MainViewModelFactory(ExpenseRepository(dao))
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeExpenses()
        observeSummary()


        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

//                R.id.nav_home -> {
//                    loadFragment(HomeFragment())
//                    true
//                }

                R.id.nav_charts -> {
                    startActivity(Intent(this, ChartsActivity::class.java))
                    true
                }

//                R.id.nav_ai -> {
//                    loadFragment(AiFragment())
//                    true
//                }

//                R.id.nav_profile -> {
//                    loadFragment(ProfileFragment())
//                    true
//                }

                else -> false
            }
        }






//        binding.fabAddExpense.setOnClickListener {
//            AddExpenseBottomSheet(viewModel)
//                .show(supportFragmentManager, "AddExpenseBottomSheet")
//        }

        binding.fabAddExpense.setOnClickListener {
            startActivity(
                Intent(this, AddTransactionActivity::class.java)
            )
        }




//        binding.fabAddExpense.setOnClickListener {
//            AddTransactionBottomSheet(viewModel)
//                .show(supportFragmentManager, "AddTransaction")
//        }


    }

    private fun setupRecyclerView() {

        expenseAdapter = ExpenseAdapter { expense ->
            val intent = Intent(this, TransactionDetailActivity::class.java)
            intent.putExtra("transaction_id", expense.id)
            startActivity(intent)
        }

        binding.rvExpenseList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = expenseAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeExpenses() {
        lifecycleScope.launch {
            viewModel.expenses.collect { expenseList ->
                expenseAdapter.submitList(expenseList)
            }
        }
    }

    private fun observeSummary() {
        lifecycleScope.launch {
            summaryViewModel.summaryState.collect { state ->
                binding.tvIncome.text = "₹ ${state.totalIncome}"
                binding.tvExpense.text = "₹ ${state.totalExpense}"
                binding.tvBalance.text = "₹ ${state.balance}"
            }
        }
    }

}
