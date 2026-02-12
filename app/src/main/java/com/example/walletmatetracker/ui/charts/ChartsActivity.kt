package com.example.walletmatetracker.ui.charts

//import android.R
import com.example.walletmatetracker.R

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walletmatetracker.ui.charts.CategoryTotal
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.data.repository.ExpenseRepository
import com.example.walletmatetracker.databinding.ActivityChartsBinding
import com.example.walletmatetracker.ui.main.MainViewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChartsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartsBinding

    private var selectedMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
    private var filterType = TimeFilterType.MONTH

    //private lateinit var monthAdapter: MonthAdapter
    private var selectedWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)








    private val viewModel: ChartsViewModel by viewModels {
        val dao = ExpenseDatabase.getDatabase(this).expenseDao()
        MainViewModelFactory(ExpenseRepository(dao))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChartsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeCharts()
        //setupMonthRecycler()
        //setupMonthDropdown()
        setupToggle()

        //observeTimeChart()

        //setupYearDropdown()



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeCharts() {

        lifecycleScope.launch {

            launch {
                viewModel.uiState.collect { state ->

                    renderPieChart(state.categoryTotals)
                    renderBarChart(state.categoryTotals)

                    binding.tvHighestCategory.text =
                        "Highest Category: ${state.highestCategory}"

                    binding.tvAverageDaily.text =
                        "Avg Daily Expense: ₹${String.format("%.2f", state.averageDailyExpense)}"

                    binding.tvMonthlyTotal.text =
                        "Total Expense: ₹${state.totalExpense}"

                }
            }

            launch {
                viewModel.timeFilteredData.collect { trendMap ->
                    renderTrendChart(trendMap)
                }
            }

        }
    }




    private fun renderPieChart(data: List<CategoryTotal>) {

        if (data.isEmpty()) {
            binding.pieChart.clear()
            return
        }

        val entries = data.map {
            PieEntry(it.totalAmount.toFloat(), it.category)
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#6042D9"),
                Color.parseColor("#886DF1"),
                Color.parseColor("#99F1AD"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#E53935"),
                Color.parseColor("#03A9F4")
            )
            valueTextSize = 13f
            valueTextColor = Color.WHITE
        }

        val pieData = PieData(dataSet)

        binding.pieChart.apply {
            this.data = pieData
            description.isEnabled = false
            setUsePercentValues(false)
            setDrawHoleEnabled(true)
            setHoleColor(Color.WHITE)
            setCenterText("Expenses")
            setCenterTextSize(16f)
            setEntryLabelColor(Color.BLACK)
            animateY(1000)
            invalidate()
        }
    }



    private fun renderBarChart(categoryTotals: List<CategoryTotal>) {

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val colors = ArrayList<Int>()

        categoryTotals.forEachIndexed { index, category ->

            entries.add(
                BarEntry(index.toFloat(), category.totalAmount.toFloat())
            )

            labels.add(category.category)

            colors.add(getSoftCategoryColor(index))
        }

        val dataSet = BarDataSet(entries, "Spending by Category")

        dataSet.colors = colors
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "₹${value.toInt()}"
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        binding.barChart.apply {

            data = barData

            description.isEnabled = false
            legend.isEnabled = false

            setFitBars(true)
            animateY(800)

            axisRight.isEnabled = false

            // X Axis
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textSize = 12f
                textColor = Color.DKGRAY
            }

            // Y Axis
            axisLeft.apply {
                textColor = Color.DKGRAY
                axisMinimum = 0f
                setDrawGridLines(true)
                gridColor = Color.parseColor("#ECECEC")
            }

            setExtraOffsets(10f, 10f, 10f, 20f)

            invalidate()
        }
    }

    private fun getSoftCategoryColor(index: Int): Int {

        val pastelColors = listOf(
            "#F8C471",  // Soft Orange
            "#82E0AA",  // Soft Green
            "#85C1E9",  // Soft Blue
            "#F5B7B1",  // Soft Pink
            "#D7BDE2",  // Soft Purple
            "#F9E79F",  // Soft Yellow
            "#AED6F1",  // Light Sky
            "#A3E4D7"   // Mint
        )

        return Color.parseColor(
            pastelColors[index % pastelColors.size]
        )
    }





    //private var filterType = TimeFilterType.MONTH

    private fun setupToggle() {

        binding.togglePeriod.addOnButtonCheckedListener { _, checkedId, isChecked ->

            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {

                binding.btnMonth.id -> {
                    filterType = TimeFilterType.MONTH
                    binding.hsPeriod.visibility = View.VISIBLE
                    renderPeriodItems(generatePastMonths())
                }

                binding.btnWeek.id -> {
                    filterType = TimeFilterType.WEEK
                    binding.hsPeriod.visibility = View.VISIBLE
                    renderPeriodItems(generatePastWeeks())
                }

                binding.btnYear.id -> {
                    filterType = TimeFilterType.YEAR
                    binding.hsPeriod.visibility = View.GONE
                    viewModel.updateFilter(
                        selectedMonth,
                        selectedYear,
                        TimeFilterType.YEAR
                    )
                }
            }
        }
    }






    private fun renderTrendChart(dataMap: Map<String, Double>) {

        if (dataMap.isEmpty()) {
            binding.lineChartTrend.clear()
            binding.tvTrendInsight.text = "No data available"
            return
        }

        val entries = ArrayList<Entry>()
        val labels = dataMap.keys.toList()

        dataMap.values.forEachIndexed { index, value ->
            entries.add(Entry(index.toFloat(), value.toFloat()))
        }

        val dataSet = LineDataSet(entries, "")

        // --- Elegant Styling ---
        dataSet.color = Color.parseColor("#6042D9")
        dataSet.lineWidth = 3f
        dataSet.setCircleColor(Color.parseColor("#6042D9"))
        dataSet.circleRadius = 6f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        // Soft Fill
        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 80
        dataSet.fillColor = Color.parseColor("#D1C6FA")

        val lineData = LineData(dataSet)

        binding.lineChartTrend.apply {

            data = lineData

            description.isEnabled = false
            legend.isEnabled = false

            axisRight.isEnabled = false

            setExtraOffsets(16f, 16f, 16f, 16f)

            axisLeft.apply {
                axisMinimum = 0f
                textColor = Color.DKGRAY
                gridColor = Color.parseColor("#EEEEEE")
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                setDrawGridLines(false)
                textColor = Color.DKGRAY
            }

            animateX(700)
            invalidate()
        }

        // Trend Insight
        if (entries.size > 1) {
            val first = entries.first().y
            val last = entries.last().y

            binding.tvTrendInsight.text = when {
                last > first -> "📈 Spending is increasing"
                last < first -> "📉 Spending is decreasing"
                else -> "➡ Spending is stable"
            }
        }
    }



//    private fun observeTimeChart() {
//        lifecycleScope.launch {
//            viewModel.timeFilteredData.collect { data ->
//                renderTimeBarChart(data)
//            }
//        }
//    }

    private fun generatePastMonths(limit: Int = 24): List<Pair<String, Pair<Int, Int>>> {

        val list = mutableListOf<Pair<String, Pair<Int, Int>>>()
        val calendar = Calendar.getInstance()

        repeat(limit) { index ->

            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            val label = when (index) {
                0 -> "This Month"
                1 -> "Last Month"
                else -> SimpleDateFormat("MMM yyyy",
                    Locale.getDefault()).format(calendar.time)
            }

            list.add(label to (month to year))

            calendar.add(Calendar.MONTH, -1)
        }

        return list
    }



    private fun generatePastWeeks(limit: Int = 6): List<Pair<String, Pair<Int, Int>>> {

        val list = mutableListOf<Pair<String, Pair<Int, Int>>>()
        val calendar = Calendar.getInstance()

        repeat(limit) { index ->

            val week = calendar.get(Calendar.WEEK_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)

            val label = when (index) {
                0 -> "This Week"
                1 -> "Last Week"
                else -> "$index Weeks Ago"
            }

            list.add(label to (week to year))

            calendar.add(Calendar.WEEK_OF_YEAR, -1)
        }

        return list
    }



    private fun renderPeriodItems(
        items: List<Pair<String, Pair<Int, Int>>>
    ) {

        binding.llPeriodContainer.removeAllViews()

        items.forEachIndexed { index, item ->

            val (label, periodData) = item
            val (value, year) = periodData

            val chip = LayoutInflater.from(this)
                .inflate(
                    R.layout.item_period_chip,
                    binding.llPeriodContainer,
                    false
                )

            val tvLabel = chip.findViewById<TextView>(R.id.tvPeriodLabel)
            val indicator = chip.findViewById<View>(R.id.viewIndicator)

            tvLabel.text = label

            // Default first selected
            if (index == 0) {
                tvLabel.setTextColor(Color.BLACK)
                indicator.visibility = View.VISIBLE
            }

            chip.setOnClickListener {

                clearAllSelection()

                tvLabel.setTextColor(Color.BLACK)
                indicator.visibility = View.VISIBLE

                selectedYear = year

                when (filterType) {

                    TimeFilterType.MONTH -> {
                        selectedMonth = value
                        viewModel.updateFilter(
                            selectedMonth,
                            selectedYear,
                            TimeFilterType.MONTH
                        )
                    }

                    TimeFilterType.WEEK -> {
                        selectedWeek = value
                        viewModel.updateFilter(
                            selectedMonth,
                            selectedYear,
                            TimeFilterType.WEEK,
                            selectedWeek
                        )
                    }

                    else -> {}
                }
            }

            binding.llPeriodContainer.addView(chip)
        }
    }

    private fun clearAllSelection() {

        for (i in 0 until binding.llPeriodContainer.childCount) {

            val item = binding.llPeriodContainer.getChildAt(i)

            val tv =
                item.findViewById<TextView>(R.id.tvPeriodLabel)

            val indicator =
                item.findViewById<View>(R.id.viewIndicator)

            tv.setTextColor(Color.parseColor("#8E98A8"))
            indicator.visibility = View.GONE
        }
    }




    private fun styleChip(card: MaterialCardView, selected: Boolean) {
        card.setCardBackgroundColor(
            if (selected) Color.BLACK else Color.WHITE
        )

        val tv = card.findViewById<TextView>(R.id.tvPeriodLabel)
        tv.setTextColor(if (selected) Color.WHITE else Color.BLACK)
    }

    private fun clearChipSelection() {
        for (i in 0 until binding.llPeriodContainer.childCount) {
            val card = binding.llPeriodContainer.getChildAt(i) as MaterialCardView
            styleChip(card, false)
        }
    }










}

