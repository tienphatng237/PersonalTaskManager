package com.example.personaltaskmanager.features.dashboard.screens

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
//import androidx.compose.ui.tooling.data.position
//import androidx.compose.ui.tooling.data.position
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.habit_tracker.viewmodel.HabitViewModel
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel
import androidx.gridlayout.widget.GridLayout
import androidx.gridlayout.widget.GridLayout.LayoutParams
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.personaltaskmanager.features.task_manager.data.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var habitViewModel: HabitViewModel

    private lateinit var tvCompletedTasks: TextView
    private lateinit var tvPendingTasks: TextView
    private lateinit var tvOverdueTasks: TextView
    private lateinit var tvCompletedThisWeek: TextView
    private lateinit var tvCompletedThisMonth: TextView
    private lateinit var tvHabitCompletionRate: TextView
    private lateinit var tvLongestStreak: TextView
    
    private lateinit var chartWeekly: LineChart
    private lateinit var chartMonthly: BarChart
    private lateinit var chartHabitStreak: LineChart
    private lateinit var gridHeatmap: GridLayout
    private lateinit var chartPriority: PieChart
    private lateinit var chartTags: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feature_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]
        habitViewModel = ViewModelProvider(requireActivity())[HabitViewModel::class.java]

        initViews(view)
        observeData()
    }

    private fun initViews(view: View) {
        tvCompletedTasks = view.findViewById(R.id.tv_completed_tasks)
        tvPendingTasks = view.findViewById(R.id.tv_pending_tasks)
        tvOverdueTasks = view.findViewById(R.id.tv_overdue_tasks)
        tvCompletedThisWeek = view.findViewById(R.id.tv_completed_this_week)
        tvCompletedThisMonth = view.findViewById(R.id.tv_completed_this_month)
        tvHabitCompletionRate = view.findViewById(R.id.tv_habit_completion_rate)
        tvLongestStreak = view.findViewById(R.id.tv_longest_streak)
        
        chartWeekly = view.findViewById(R.id.chart_weekly)
        chartMonthly = view.findViewById(R.id.chart_monthly)
        chartHabitStreak = view.findViewById(R.id.chart_habit_streak)
        gridHeatmap = view.findViewById(R.id.grid_heatmap)
        chartPriority = view.findViewById(R.id.chart_priority)
        chartTags = view.findViewById(R.id.chart_tags)

        setupCharts()
    }
    
    private fun setupCharts() {
        setupWeeklyChart()
        setupMonthlyChart()
        setupHabitStreakChart()
        setupPriorityChart()
        setupTagsChart()
    }
    
    private fun setupWeeklyChart() {
        chartWeekly.description.isEnabled = false
        chartWeekly.setTouchEnabled(true)
        chartWeekly.setDragEnabled(true)
        chartWeekly.setScaleEnabled(false)
        chartWeekly.setPinchZoom(false)
        chartWeekly.legend.isEnabled = false
        
        val xAxis = chartWeekly.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        
        val leftAxis = chartWeekly.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        
        chartWeekly.axisRight.isEnabled = false
    }
    
    private fun setupMonthlyChart() {
        chartMonthly.description.isEnabled = false
        chartMonthly.setTouchEnabled(true)
        chartMonthly.setDragEnabled(true)
        chartMonthly.setScaleEnabled(false)
        chartMonthly.setPinchZoom(false)
        chartMonthly.legend.isEnabled = false
        
        val xAxis = chartMonthly.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        
        val leftAxis = chartMonthly.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        
        chartMonthly.axisRight.isEnabled = false
    }
    
    private fun setupHabitStreakChart() {
        chartHabitStreak.description.isEnabled = false
        chartHabitStreak.setTouchEnabled(true)
        chartHabitStreak.setDragEnabled(true)
        chartHabitStreak.setScaleEnabled(false)
        chartHabitStreak.setPinchZoom(false)
        chartHabitStreak.legend.isEnabled = false
        
        val xAxis = chartHabitStreak.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        
        val leftAxis = chartHabitStreak.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        
        chartHabitStreak.axisRight.isEnabled = false
    }

    private fun observeData() {
        // Task statistics
        taskViewModel.getCompletedTasksCount().observe(viewLifecycleOwner) { count ->
            tvCompletedTasks.text = count.toString()
        }

        taskViewModel.getPendingTasksCount().observe(viewLifecycleOwner) { count ->
            tvPendingTasks.text = count.toString()
        }

        taskViewModel.getOverdueTasksCount().observe(viewLifecycleOwner) { count ->
            tvOverdueTasks.text = count.toString()
        }

        // Completed this week
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val weekEnd = calendar.timeInMillis

        taskViewModel.getCompletedTasksCountByDate(weekStart, weekEnd).observe(viewLifecycleOwner) { count ->
            tvCompletedThisWeek.text = count.toString()
            updateWeeklyChart()
        }

        // Completed this month
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val monthEnd = calendar.timeInMillis

        taskViewModel.getCompletedTasksCountByDate(monthStart, monthEnd).observe(viewLifecycleOwner) { count ->
            tvCompletedThisMonth.text = count.toString()
            updateMonthlyChart()
        }

        // Habit statistics
        habitViewModel.getAllHabits().observe(viewLifecycleOwner) { habits ->
            if (habits.isNullOrEmpty()) {
                tvHabitCompletionRate.text = "0%"
                tvLongestStreak.text = "0"
                return@observe
            }

            // Calculate average completion rate
            var totalCompletion = 0.0
            var habitsWithData = 0
            var longestStreak = 0

            for (habit in habits) {
                if (habit.endDate > 0 && habit.startDate > 0) {
                    val totalDays = ((habit.endDate - habit.startDate) / 86400000L) + 1
                    if (totalDays > 0) {
                        // Get completions count (simplified - should use actual completion data)
                        val completionRate = (habit.streakDays.toDouble() / totalDays) * 100
                        totalCompletion += completionRate
                        habitsWithData++
                    }
                }
                if (habit.streakDays > longestStreak) {
                    longestStreak = habit.streakDays
                }
            }

            val avgCompletion = if (habitsWithData > 0) {
                (totalCompletion / habitsWithData).toInt()
            } else {
                0
            }

            tvHabitCompletionRate.text = "$avgCompletion%"
            tvLongestStreak.text = longestStreak.toString()
            updateHabitStreakChart(habits)
        }

        // Update heatmap and tag/priority charts
        taskViewModel.getCompletedTasks().observe(viewLifecycleOwner) { tasks ->
            updateHeatmapCalendar(tasks)
            updatePriorityChart(tasks)
            updateTagsChart(tasks)
        }
    }

    private fun updateWeeklyChart() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val entries = mutableListOf<Entry>()
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val labels = mutableListOf<String>()

        for (i in 0..6) {
            val dayStart = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = calendar.timeInMillis - 1

            // Get completed tasks for this day
            taskViewModel.getCompletedTasksCountByDate(dayStart, dayEnd).observe(viewLifecycleOwner) { count ->
                entries.add(Entry(i.toFloat(), count.toFloat()))
                labels.add(dateFormat.format(Date(dayStart)))

                if (entries.size == 7) {
                    val dataSet = LineDataSet(entries, "Hoàn thành")
                    dataSet.color = Color.parseColor("#2196F3")
                    dataSet.valueTextColor = Color.BLACK
                    dataSet.setCircleColor(Color.parseColor("#2196F3"))
                    dataSet.lineWidth = 2f
                    dataSet.circleRadius = 4f
                    dataSet.setDrawValues(false)

                    val lineData = LineData(dataSet)
                    chartWeekly.data = lineData

                    val xAxis = chartWeekly.xAxis
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index >= 0 && index < labels.size) labels[index] else ""
                        }
                    }

                    chartWeekly.invalidate()
                }
            }
        }
    }
    
    private fun updateMonthlyChart() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        
        // Sample data for first 7 days, last 7 days, and middle
        val sampleDays = listOf(1, 7, 14, 21, daysInMonth)
        
        for ((index, day) in sampleDays.withIndex()) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val dayStart = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = calendar.timeInMillis - 1
            
            taskViewModel.getCompletedTasksCountByDate(dayStart, dayEnd).observe(viewLifecycleOwner) { count ->
                entries.add(BarEntry(index.toFloat(), count.toFloat()))
                labels.add("$day")
                
                if (entries.size == sampleDays.size) {
                    val dataSet = BarDataSet(entries, "Hoàn thành")
                    dataSet.color = Color.parseColor("#4CAF50")
                    dataSet.valueTextColor = Color.BLACK
                    
                    val barData = BarData(dataSet)
                    chartMonthly.data = barData
                    
                    val xAxis = chartMonthly.xAxis
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index >= 0 && index < labels.size) labels[index] else ""
                        }
                    }

                    chartMonthly.invalidate()
                }
            }
        }
    }
    
//    private fun updateHabitStreakChart(habits: List<com.example.personaltaskmanager.features.habit_tracker.data.model.Habit>) {
//        val entries = mutableListOf<Entry>()
//        val labels = mutableListOf<String>()
//
//        habits.sortedByDescending { it.streakDays }.take(5).forEachIndexed { index, habit ->
//            entries.add(Entry(index.toFloat(), habit.streakDays.toFloat()))
//            labels.add(habit.title.take(10))
//        }
//
//        if (entries.isNotEmpty()) {
//            val dataSet = LineDataSet(entries, "Streak")
//            dataSet.color = Color.parseColor("#FFD700")
//            dataSet.valueTextColor = Color.BLACK
//            dataSet.setCircleColor(Color.parseColor("#FFD700"))
//            dataSet.lineWidth = 2f
//            dataSet.circleRadius = 4f
//            dataSet.setDrawValues(true)
//
//            val lineData = LineData(dataSet)
//            chartHabitStreak.data = lineData
//
//            val xAxis = chartHabitStreak.xAxis
//            xAxis.valueFormatter = object : ValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    val index = value.toInt()
//                    return if (index >= 0 && index < labels.size) labels[index] else ""
//                }
//            }
//
//            chartHabitStreak.invalidate()
//        }
//    }
    private fun updateHabitStreakChart(habits: List<com.example.personaltaskmanager.features.habit_tracker.data.model.Habit>) {
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        // 1. Lấy dữ liệu Top 5 thói quen có streak cao nhất để biểu đồ không bị quá dày
        val topHabits = habits.sortedByDescending { it.streakDays }.take(5)

        topHabits.forEachIndexed { index, habit ->
            // Gán index vào trục X (0, 1, 2...) và streakDays vào trục Y
            entries.add(Entry(index.toFloat(), habit.streakDays.toFloat()))
            labels.add(habit.title.take(10)) // Giới hạn 10 ký tự để không tràn nhãn
        }

        if (entries.isNotEmpty()) {
            // 2. Cấu hình DataSet (Đường biểu diễn)
            val dataSet = LineDataSet(entries, "Chuỗi Streak")
            dataSet.color = Color.parseColor("#FFD700")       // Màu vàng Gold
            dataSet.setCircleColor(Color.parseColor("#FFD700"))
            dataSet.valueTextColor = Color.BLACK
            dataSet.lineWidth = 2.5f                          // Độ dày đường kẻ
            dataSet.circleRadius = 5f                         // Độ lớn điểm nút
            dataSet.setDrawValues(true)                       // Hiển thị số streak trên điểm nút
            dataSet.valueTextSize = 10f

            // Tạo hiệu ứng đổ bóng phía dưới đường kẻ cho đẹp
            dataSet.setDrawFilled(true)
            dataSet.fillColor = Color.parseColor("#4DFFD700") // Màu vàng nhạt trong suốt

            val lineData = LineData(dataSet)
            chartHabitStreak.data = lineData

            // 3. Cấu hình TRỤC X (QUAN TRỌNG NHẤT ĐỂ SỬA LỖI LẶP TÊN)
            val xAxis = chartHabitStreak.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)

            // Các dòng code giải quyết lỗi lặp nhãn:
            xAxis.granularity = 1f              // Ép khoảng cách tối thiểu giữa các nhãn là 1 đơn vị
            xAxis.isGranularityEnabled = true   // Kích hoạt tính năng chặn chia nhỏ nhãn (0.5, 1.5...)
            xAxis.labelCount = labels.size      // Chỉ định rõ số lượng nhãn hiển thị tương ứng với số cột

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    // Kiểm tra điều kiện để tránh lỗi IndexOutOfBounds
                    return if (index >= 0 && index < labels.size) {
                        labels[index]
                    } else {
                        ""
                    }
                }
            }

            // 4. Làm mới biểu đồ với hiệu ứng
            chartHabitStreak.animateX(1000) // Hiệu ứng chạy từ trái sang phải
            chartHabitStreak.invalidate()
        } else {
            chartHabitStreak.clear() // Xóa trắng biểu đồ nếu không có dữ liệu
        }
    }


    private fun setupPriorityChart() {
        chartPriority.description.isEnabled = false
        chartPriority.setTouchEnabled(true)
        chartPriority.legend.isEnabled = true
        chartPriority.legend.textColor = Color.BLACK
    }

    private fun setupTagsChart() {
        chartTags.description.isEnabled = false
        chartTags.setTouchEnabled(true)
        chartTags.setDragEnabled(true)
        chartTags.setScaleEnabled(false)
        chartTags.setPinchZoom(false)
        chartTags.legend.isEnabled = false

        val xAxis = chartTags.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)

        val leftAxis = chartTags.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f

        chartTags.axisRight.isEnabled = false
    }

    private fun updateHeatmapCalendar(tasks: List<Task>) {
        gridHeatmap.removeAllViews()

        // Calculate completion count per day for last 42 days (6 weeks)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, -41) // Start from 42 days ago

        val dayCounts = mutableMapOf<Long, Int>()
        val dayInMillis = 86400000L

        for (task in tasks) {
            if (task.isCompleted && task.createdAt > 0) {
                val day = (task.createdAt / dayInMillis) * dayInMillis
                dayCounts[day] = (dayCounts[day] ?: 0) + 1
            }
        }

        // Find max count for color scaling
        val maxCount = dayCounts.values.maxOrNull() ?: 1

        // Create grid cells
        for (i in 0..41) {
            val day = calendar.timeInMillis
            val count = dayCounts[day] ?: 0
            val intensity = if (maxCount > 0) (count.toFloat() / maxCount) else 0f

            val cell = View(requireContext())
            val cellSize = 40.dpToPx()
            val params = LayoutParams(
                GridLayout.spec(i / 7, 1f),
                GridLayout.spec(i % 7, 1f)
            ).apply {
                width = cellSize
                height = cellSize
                setMargins(2, 2, 2, 2)
            }

            // Color based on intensity
            val color = when {
                count == 0 -> Color.parseColor("#E3F2FD")
                intensity < 0.2f -> Color.parseColor("#90CAF9")
                intensity < 0.4f -> Color.parseColor("#42A5F5")
                intensity < 0.6f -> Color.parseColor("#1E88E5")
                intensity < 0.8f -> Color.parseColor("#1565C0")
                else -> Color.parseColor("#0D47A1")
            }

            cell.setBackgroundColor(color)
            gridHeatmap.addView(cell, params)

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun updatePriorityChart(tasks: List<Task>) {
        val highCount = tasks.count { it.priority == "high" && it.isCompleted }
        val mediumCount = tasks.count { it.priority == "medium" && it.isCompleted }
        val lowCount = tasks.count { it.priority == "low" && it.isCompleted }
        val noPriorityCount = tasks.count { (it.priority == null || it.priority.isEmpty()) && it.isCompleted }

        val entries = mutableListOf<PieEntry>()
        if (highCount > 0) entries.add(PieEntry(highCount.toFloat(), "High"))
        if (mediumCount > 0) entries.add(PieEntry(mediumCount.toFloat(), "Medium"))
        if (lowCount > 0) entries.add(PieEntry(lowCount.toFloat(), "Low"))
        if (noPriorityCount > 0) entries.add(PieEntry(noPriorityCount.toFloat(), "None"))

        if (entries.isNotEmpty()) {
            val dataSet = PieDataSet(entries, "Priority")
            dataSet.colors = listOf(
                Color.parseColor("#F44336"), // High - Red
                Color.parseColor("#FF9800"), // Medium - Orange
                Color.parseColor("#4CAF50"), // Low - Green
                Color.parseColor("#9E9E9E")  // None - Gray
            )
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 12f

            val pieData = PieData(dataSet)
            chartPriority.data = pieData
            chartPriority.invalidate()
        }
    }

//    private fun updateTagsChart(tasks: List<Task>) {
//        // Count tasks by tag
//        val tagCounts = mutableMapOf<String, Int>()
//        for (task in tasks) {
//            if (task.isCompleted) {
//                val tags = task.getTagsList()
//                for (tag in tags) {
//                    if (tag.isNotEmpty()) {
//                        tagCounts[tag] = (tagCounts[tag] ?: 0) + 1
//                    }
//                }
//            }
//        }
//
//        // Get top 5 tags
//        val topTags = tagCounts.toList().sortedByDescending { it.second }.take(5)
//
//        if (topTags.isNotEmpty()) {
//            val entries = mutableListOf<BarEntry>()
//            val labels = mutableListOf<String>()
//
//            topTags.forEachIndexed { index, (tag, count) ->
//                entries.add(BarEntry(index.toFloat(), count.toFloat()))
//                labels.add(tag.take(10))
//            }
//
//            val dataSet = BarDataSet(entries, "Tags")
//            dataSet.color = Color.parseColor("#2196F3")
//            dataSet.valueTextColor = Color.BLACK
//
//            val barData = BarData(dataSet)
//            chartTags.data = barData
//
//            val xAxis = chartTags.xAxis
//            xAxis.valueFormatter = object : ValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    val index = value.toInt()
//                    return if (index >= 0 && index < labels.size) labels[index] else ""
//                }
//            }
//
//            chartTags.invalidate()
//        }
//    }

    private fun updateTagsChart(tasks: List<Task>) {
        // 1. Đếm số lượng task theo tag
        val tagCounts = mutableMapOf<String, Int>()
        for (task in tasks) {
            if (task.isCompleted) {
                val tags = task.getTagsList()
                for (tag in tags) {
                    if (tag.isNotEmpty()) {
                        tagCounts[tag] = (tagCounts[tag] ?: 0) + 1
                    }
                }
            }
        }

        // 2. Lấy Top 5 tags nhiều nhất
        val topTags = tagCounts.toList().sortedByDescending { it.second }.take(5)

        if (topTags.isNotEmpty()) {
            val entries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            topTags.forEachIndexed { index, (tag, count) ->
                entries.add(BarEntry(index.toFloat(), count.toFloat()))
                labels.add(tag.take(10)) // Giới hạn 10 ký tự để không bị tràn
            }

            val dataSet = BarDataSet(entries, "Tags")
            dataSet.color = Color.parseColor("#2196F3")
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 10f

            val barData = BarData(dataSet)
            chartTags.data = barData

            // 3. Cấu hình Trục X (CHÂN TRỊ ĐỂ HẾT LẶP TÊN)
            val xAxis = chartTags.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)

            // QUAN TRỌNG: Ngăn chặn lặp nhãn
            xAxis.granularity = 1f           // Chỉ cho phép bước nhảy là 1 đơn vị
            xAxis.isGranularityEnabled = true // Kích hoạt chặn bước nhảy lẻ
            xAxis.labelCount = labels.size    // Ép hiển thị đúng số lượng nhãn đang có

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    // Kiểm tra index hợp lệ để tránh Crash
                    return if (index >= 0 && index < labels.size) {
                        labels[index]
                    } else {
                        ""
                    }
                }
            }

            // 4. Làm mới biểu đồ
            chartTags.setFitBars(true) // Làm các cột khít với chiều rộng
            chartTags.animateY(1000)   // Thêm hiệu ứng cho đẹp
            chartTags.invalidate()
        } else {
            chartTags.clear() // Xóa biểu đồ nếu không có dữ liệu
        }
    }



    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}

