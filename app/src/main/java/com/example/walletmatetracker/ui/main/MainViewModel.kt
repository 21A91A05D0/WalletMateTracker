package com.example.walletmatetracker.ui.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    /* ---------------- UI STATE ---------------- */
    private val selectedCategory = MutableStateFlow<String>("All")

    fun updateCategoryFilter(category: String) {
        selectedCategory.value = category
    }



    private val selectedMonth =
        MutableStateFlow(
            MonthFilter(
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.YEAR)
            )
        )

    private val monthOnlyTransactions: StateFlow<List<ExpenseEntity>> =
        combine(
            repository.getAllExpenses(),
            selectedMonth
        ) { transactions, monthFilter ->

            val cal = Calendar.getInstance()

            transactions.filter { transaction ->

                cal.timeInMillis = transaction.date

                cal.get(Calendar.MONTH) == monthFilter.month &&
                        cal.get(Calendar.YEAR) == monthFilter.year
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    fun updateMonth(month: Int, year: Int) {
        selectedMonth.value = MonthFilter(month, year)
        selectedStartDate.value = null
    }

    private val selectedDateFilter =
        MutableStateFlow(WeeklyFilterType.ALL)

    fun updateWeeklyFilter(filter: WeeklyFilterType) {
        selectedDateFilter.value = filter
    }


    private val selectedStartDate =
        MutableStateFlow<Long?>(null)

    fun updateStartDate(date: Long?) {
        selectedStartDate.value = date
    }



//    private val selectedDateFilter =
//        MutableStateFlow(DateFilterType.ALL)
//
//    fun updateDateFilter(filter: DateFilterType) {
//        selectedDateFilter.value = filter
//    }

//    val expenses: StateFlow<List<ExpenseEntity>> =
//        repository.getAllExpenses()
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000),
//                initialValue = emptyList()
//            )



    val filteredTransactions: StateFlow<List<ExpenseEntity>> =
        combine(
            monthOnlyTransactions,
            selectedCategory,
            selectedDateFilter,
            selectedStartDate
        ) { transactions, category, weeklyFilter, startDate ->

            val cal = Calendar.getInstance()

            transactions.filter { transaction ->

                cal.timeInMillis = transaction.date

                // CATEGORY FILTER
                val categoryMatches =
                    category == "All" ||
                            transaction.category == category

                // WEEK FILTER
                val weekMatches = when (weeklyFilter) {
                    WeeklyFilterType.ALL -> true
                    WeeklyFilterType.THIS_WEEK -> {
                        val now = Calendar.getInstance()
                        cal.get(Calendar.WEEK_OF_YEAR) ==
                                now.get(Calendar.WEEK_OF_YEAR)
                    }
                }

                // DATE THRESHOLD FILTER
                val dateMatches =
                    startDate == null ||
                            transaction.date >= startDate

                categoryMatches && weekMatches && dateMatches
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )




//    val monthlySummary: StateFlow<SummaryState> =
//        filteredTransactions
//            .map { transactions ->
//
//                val income =
//                    transactions
//                        .filter {
//                            it.transactionType == TransactionType.INCOME
//                        }
//                        .sumOf { it.amount }
//
//                val expense =
//                    transactions
//                        .filter {
//                            it.transactionType == TransactionType.EXPENSE
//                        }
//                        .sumOf { it.amount }
//
//                SummaryState(
//                    totalIncome = income,
//                    totalExpense = expense,
//                    balance = income - expense
//                )
//            }
//            .stateIn(
//                viewModelScope,
//                SharingStarted.WhileSubscribed(5000),
//                SummaryState()
//            )

    val monthlySummary: StateFlow<SummaryState> =
        monthOnlyTransactions
            .map { transactions ->

                val income =
                    transactions
                        .filter {
                            it.transactionType == TransactionType.INCOME
                        }
                        .sumOf { it.amount }

                val expense =
                    transactions
                        .filter {
                            it.transactionType == TransactionType.EXPENSE
                        }
                        .sumOf { it.amount }

                SummaryState(
                    totalIncome = income,
                    totalExpense = expense,
                    balance = income - expense
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                SummaryState()
            )









    val categoryWiseSpending =
        repository.getCategoryWiseSpending()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val averageExpense =
        repository.getAverageExpense()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

    /* ---------------- ACTIONS ---------------- */

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }

    suspend fun getExpenseById(id: String): ExpenseEntity? {
        return repository.getExpenseById(id)
    }


    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun getExpensesBetweenDates(
        startDate: Long,
        endDate: Long
    ): StateFlow<List<ExpenseEntity>> {
        return repository.getExpensesBetweenDates(startDate, endDate)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }
}
