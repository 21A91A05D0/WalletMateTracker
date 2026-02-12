package com.example.walletmatetracker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.ui.charts.ChartsViewModel
import com.example.walletmatetracker.ui.home.HomeSummaryViewModel

class MainViewModelFactory(
    private val repository: ExpenseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(HomeSummaryViewModel::class.java) -> {
                HomeSummaryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ChartsViewModel::class.java) ->
                ChartsViewModel(repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
