package com.example.walletmatetracker.ui.util


import com.example.walletmatetracker.R

object CategoryIconMapper {

    fun getIcon(category: String): Int {
        return when (category.uppercase()) {

            "FOOD" -> R.drawable.ic_food
            "TRANSPORT" -> R.drawable.ic_transport
            "BILLS" -> R.drawable.ic_bills
            "ENTERTAINMENT" -> R.drawable.ic_enter
            "SHOPPING" -> R.drawable.ic_shopping
            "EDUCATION" -> R.drawable.ic_education
            "HEALTH" -> R.drawable.ic_health
            "SALARY" -> R.drawable.ic_salary
            "PART_TIME" -> R.drawable.ic_salary
            "BONUS" -> R.drawable.ic_salary
            "INVESTMENTS" -> R.drawable.ic_salary

            else -> R.drawable.ic_other
        }
    }
}
