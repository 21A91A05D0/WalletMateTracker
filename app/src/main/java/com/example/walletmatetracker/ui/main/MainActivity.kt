package com.example.walletmatetracker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walletmatetracker.R
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.databinding.ActivityMainBinding
import com.example.walletmatetracker.domain.model.ExpenseCategory
import com.example.walletmatetracker.domain.model.IncomeCategory
import com.example.walletmatetracker.domain.model.TransactionType
import kotlinx.coroutines.launch

import com.example.walletmatetracker.ui.addtransaction.AddTransactionActivity
import com.example.walletmatetracker.ui.addtransaction.TransactionDetailActivity
import com.example.walletmatetracker.ui.analytics.AnalyticsActivity
import com.example.walletmatetracker.ui.home.HomeSummaryViewModel

import com.example.walletmatetracker.ui.charts.ChartsActivity
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
        setupCategoryChips()
        setupDateFilter()
        setupMonthSelector()


        binding.tvClearDate.setOnClickListener {

            viewModel.updateStartDate(null)

            binding.cardDateBadge.visibility = View.GONE
        }







//        binding.bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//
////                R.id.nav_home -> {
////                    loadFragment(HomeFragment())
////                    true
////                }
//
//                R.id.nav_charts -> {
//                    startActivity(Intent(this, ChartsActivity::class.java))
//                    true
//                }
//
//                R.id.navAnalytics -> {
//                    startActivity(Intent(this, AnalyticsActivity::class.java))
//                    true
//                }
//
//
//
////                R.id.nav_profile -> {
////                    loadFragment(ProfileFragment())
////                    true
////                }
//
//                else -> false
//            }
//        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> true

                R.id.nav_charts -> {
                    val intent = Intent(this, ChartsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    true
                }

                R.id.navAnalytics -> {
                    val intent = Intent(this, AnalyticsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    true
                }

//                R.id.navProfile -> {
//                    val intent = Intent(this, ProfileActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                    startActivity(intent)
//                    overridePendingTransition(
//                        R.anim.slide_in_right,
//                        R.anim.slide_out_left
//                    )
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
            viewModel.filteredTransactions.collect { expenseList ->
                expenseAdapter.submitList(expenseList)
            }
        }
    }

    private fun observeSummary() {

        lifecycleScope.launch {
            viewModel.monthlySummary.collect { summary ->

                binding.tvBalance.text = "₹ ${summary.balance}"
                binding.tvIncome.text = "₹ ${summary.totalIncome}"
                binding.tvExpense.text = "₹ ${summary.totalExpense}"
            }
        }




//        lifecycleScope.launch {
//            viewModel.transactions.collect { list ->
//
//                val income =
//                    list.filter {
//                        it.transactionType == TransactionType.INCOME
//                    }.sumOf { it.amount }
//
//                val expense =
//                    list.filter {
//                        it.transactionType == TransactionType.EXPENSE
//                    }.sumOf { it.amount }
//
//                val balance = income - expense
//
//                binding.tvBalance.text = "₹ $balance"
//                binding.tvIncome.text = "₹ $income"
//                binding.tvExpense.text = "₹ $expense"
//            }
//        }



    }

    private fun setupCategoryChips() {

        val categories = mutableListOf("All")

        categories.addAll(
            ExpenseCategory.values().map { it.name }
        )

        categories.addAll(
            IncomeCategory.values().map { it.name }
        )

        categories.distinct().forEach { category ->

            val chip = Chip(this).apply {
                text = category
                isCheckable = true
                isClickable = true
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
                chipMinHeight = 40f
                setEnsureMinTouchTargetSize(false)
                isCheckedIconVisible = false
                setChipBackgroundColorResource(R.color.chip_selector)
                setTextColor(resources.getColorStateList(R.color.chip_text_selector))
            }

            binding.chipGroupCategories.addView(chip)
        }

        // Default select "All"
        (binding.chipGroupCategories.getChildAt(0) as Chip).isChecked = true

        binding.chipGroupCategories.setOnCheckedChangeListener { group, checkedId ->
            val selectedChip =
                group.findViewById<Chip>(checkedId)

            selectedChip?.let {
                viewModel.updateCategoryFilter(it.text.toString())
            }
        }
    }

    private fun setupDateFilter() {

        binding.cardWeeklyFilter.setOnClickListener {

            val popup = PopupMenu(this, binding.cardWeeklyFilter)

            popup.menu.add("All Weeks")
            popup.menu.add("This Week")

            popup.setOnMenuItemClickListener {

                binding.tvWeeklyFilter.text = it.title

                val filter =
                    if (it.title == "This Week")
                        WeeklyFilterType.THIS_WEEK
                    else
                        WeeklyFilterType.ALL

                viewModel.updateWeeklyFilter(filter)
                true
            }

            popup.show()
        }


//        binding.cardDateFilter.setOnClickListener {
//
//            val popup = PopupMenu(this, binding.cardDateFilter)
//
//            popup.menu.add("All Time")
//            popup.menu.add("Today")
//            popup.menu.add("This Week")
//            popup.menu.add("This Month")
//            popup.menu.add("Last Month")
//
//            popup.setOnMenuItemClickListener {
//
//                binding.tvDateFilter.text = it.title
//
//                val filter = when (it.title) {
//                    "Today" -> DateFilterType.TODAY
//                    "This Week" -> DateFilterType.THIS_WEEK
//                    "This Month" -> DateFilterType.THIS_MONTH
//                    "Last Month" -> DateFilterType.LAST_MONTH
//                    else -> DateFilterType.ALL
//                }
//
//                //viewModel.updateDateFilter(filter)
//                true
//            }
//
//            popup.show()
//        }
    }

    private fun setupMonthSelector() {

        binding.tvMonthSelector.setOnClickListener {

            val current = Calendar.getInstance()

            val dialog = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Month")
                .setSelection(current.timeInMillis)
                .build()

//            dialog.addOnPositiveButtonClickListener { selection ->
//
//                val cal = Calendar.getInstance()
//                cal.timeInMillis = selection
//
//                val month = cal.get(Calendar.MONTH)
//                val year = cal.get(Calendar.YEAR)
//
//                viewModel.updateMonth(month, year)
//
//                // 🔥 NEW LINE
//                viewModel.updateStartDate(selection)
//
//                binding.tvMonthSelector.text =
//                    SimpleDateFormat("dd MMM yyyy",
//                        Locale.getDefault()
//                    ).format(cal.time)
//            }

                dialog.addOnPositiveButtonClickListener { selection ->

                    val cal = Calendar.getInstance()
                    cal.timeInMillis = selection

                    val month = cal.get(Calendar.MONTH)
                    val year = cal.get(Calendar.YEAR)

                    viewModel.updateMonth(month, year)
                    viewModel.updateStartDate(selection)

                    val formatted =
                        SimpleDateFormat("dd MMM",
                            Locale.getDefault()
                        ).format(cal.time)

                    binding.cardDateBadge.visibility = View.VISIBLE
                    binding.tvDateBadge.text = "From $formatted"
                }




            dialog.show(supportFragmentManager, "MONTH_PICKER")
        }
    }







}
