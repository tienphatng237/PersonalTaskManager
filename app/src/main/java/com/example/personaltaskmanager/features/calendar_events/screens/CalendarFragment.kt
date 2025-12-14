package com.example.personaltaskmanager.features.calendar_events.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.calendar_events.viewmodel.CalendarViewModel
import com.example.personaltaskmanager.features.task_manager.data.model.Task
import com.example.personaltaskmanager.features.task_manager.screens.TaskAdapter
import com.example.personaltaskmanager.features.task_manager.screens.TaskDetailActivity
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlock
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlockParser
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * CalendarFragment
 * ----------------
 * Màn hình Calendar chính của ứng dụng.
 *
 * LƯU Ý KIẾN TRÚC:
 * - TaskViewModel: dùng để HIỂN THỊ task theo ngày (logic cũ, giữ nguyên)
 * - CalendarViewModel: chuẩn bị cho việc hiển thị CalendarEvent độc lập
 *
 * Phiên bản hiện tại:
 * - Calendar hiển thị task + todo con (group theo task)
 * - Có bộ lọc All / Công việc / Việc
 * - Có dấu chấm hiển thị ngày có dữ liệu (Task / Todo)
 */
class CalendarFragment : Fragment() {

    // ===== UI =====
    private lateinit var tvMonthTitle: TextView
    private lateinit var tvSelectedDate: TextView
    private lateinit var rvCalendar: RecyclerView
    private lateinit var rvTasks: RecyclerView
    private lateinit var rvTodos: RecyclerView

    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton

    private lateinit var btnFilterAll: TextView
    private lateinit var btnFilterTask: TextView
    private lateinit var btnFilterTodo: TextView
    private lateinit var filterHighlight: View

    // ===== ADAPTER =====
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var todoAdapter: CalendarTodoAdapter
    private lateinit var calendarDayAdapter: CalendarDayAdapter

    // ===== VIEWMODEL =====
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var calendarViewModel: CalendarViewModel

    // ===== STATE =====
    private var selectedDate: LocalDate = LocalDate.now()
    private var currentMonth: YearMonth = YearMonth.now()

    private enum class FilterMode { ALL, TASK, TODO }
    private var currentFilter = FilterMode.ALL

    private val REQUEST_EDIT_TASK = 3010

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.feature_calendar_month, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== BIND VIEW =====
        tvMonthTitle = view.findViewById(R.id.tv_month_title)
        tvSelectedDate = view.findViewById(R.id.tv_selected_date)

        rvCalendar = view.findViewById(R.id.rv_calendar_days)
        rvTasks = view.findViewById(R.id.rv_tasks_of_day)
        rvTodos = view.findViewById(R.id.rv_todos_of_day)

        btnPrev = view.findViewById(R.id.btn_prev_month)
        btnNext = view.findViewById(R.id.btn_next_month)

        btnFilterAll = view.findViewById(R.id.btn_filter_all)
        btnFilterTask = view.findViewById(R.id.btn_filter_task)
        btnFilterTodo = view.findViewById(R.id.btn_filter_todo)
        filterHighlight = view.findViewById(R.id.view_filter_highlight)

        // ===== LAYOUT =====
        rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTodos.layoutManager = LinearLayoutManager(requireContext())

        // ===== ADAPTER =====
        taskAdapter = TaskAdapter(
            { task -> openEditTask(task) },
            { task -> taskViewModel.deleteTask(task) },
            { task, done -> taskViewModel.toggleCompleted(task, done) }
        )
        todoAdapter = CalendarTodoAdapter()

        rvTasks.adapter = taskAdapter
        rvTodos.adapter = todoAdapter

        // ===== VIEWMODEL =====
        taskViewModel =
            ViewModelProvider(requireActivity())[TaskViewModel::class.java]
        calendarViewModel =
            ViewModelProvider(this)[CalendarViewModel::class.java]

        // ===== COLOR =====
        val primaryColor =
            ContextCompat.getColor(requireContext(), R.color.calendar_primary)
        btnPrev.setColorFilter(primaryColor)
        btnNext.setColorFilter(primaryColor)

        // ===== FILTER =====
        setupFilter()

        // ===== NAV MONTH =====
        btnPrev.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            loadMonth()
        }

        btnNext.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            loadMonth()
        }

        loadMonth()
        reloadData()
    }

    private fun setupFilter() {
        btnFilterAll.setOnClickListener { switchFilter(FilterMode.ALL, btnFilterAll) }
        btnFilterTask.setOnClickListener { switchFilter(FilterMode.TASK, btnFilterTask) }
        btnFilterTodo.setOnClickListener { switchFilter(FilterMode.TODO, btnFilterTodo) }

        btnFilterAll.post {
            moveHighlight(btnFilterAll, animate = false)
        }
    }

    private fun switchFilter(mode: FilterMode, targetView: TextView) {
        if (currentFilter == mode) return

        moveHighlight(targetView, animate = true)
        currentFilter = mode
        reloadData()
    }

    private fun moveHighlight(target: TextView, animate: Boolean) {
        filterHighlight.layoutParams.width = target.width
        filterHighlight.requestLayout()

        if (animate) {
            filterHighlight.animate()
                .x(target.x)
                .setDuration(200)
                .start()
        } else {
            filterHighlight.x = target.x
        }
    }

    private fun reloadData() {

        val animOut = AnimationUtils.loadAnimation(
            requireContext(), R.anim.fade_slide_out_down
        )
        val animIn = AnimationUtils.loadAnimation(
            requireContext(), R.anim.fade_slide_in_up
        )

        when (currentFilter) {

            FilterMode.ALL -> {
                rvTasks.clearAnimation()
                rvTodos.clearAnimation()
                rvTasks.visibility = View.VISIBLE
                rvTodos.visibility = View.VISIBLE
                rvTasks.startAnimation(animIn)
                rvTodos.startAnimation(animIn)
            }

            FilterMode.TASK -> {
                if (rvTodos.visibility == View.VISIBLE) {
                    rvTodos.startAnimation(animOut)
                    rvTodos.visibility = View.GONE
                }
                rvTasks.visibility = View.VISIBLE
                rvTasks.startAnimation(animIn)
            }

            FilterMode.TODO -> {
                if (rvTasks.visibility == View.VISIBLE) {
                    rvTasks.startAnimation(animOut)
                    rvTasks.visibility = View.GONE
                }
                rvTodos.visibility = View.VISIBLE
                rvTodos.startAnimation(animIn)
            }
        }

        loadTasksAndTodosOfDate(selectedDate)
    }

    private fun loadMonth() {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        tvMonthTitle.text = currentMonth.format(formatter)
        tvSelectedDate.text = "Công việc ngày: $selectedDate"

        val startMonth =
            currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMonth =
            currentMonth.atEndOfMonth().plusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        taskViewModel.getTasksByDate(startMonth, endMonth)
            .observe(viewLifecycleOwner) { tasks ->

                val days = generateCalendarDays(currentMonth)

                val taskDates = tasks.mapNotNull { task ->
                    task.deadline?.let { millis ->
                        java.time.Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                }.toSet()

                days.forEach { day ->
                    day.hasEvent = taskDates.contains(day.date)
                }

                calendarDayAdapter = CalendarDayAdapter(
                    days = days,
                    selectedDate = selectedDate
                ) { clickedDay ->
                    if (clickedDay.isValid) {
                        selectedDate = clickedDay.date
                        tvSelectedDate.text = "Công việc ngày: $selectedDate"
                        calendarDayAdapter.updateSelectedDate(selectedDate)
                        reloadData()
                    }
                }

                rvCalendar.adapter = calendarDayAdapter
            }
    }

    /**
     * Load task + todo con theo ngày
     */
    private fun loadTasksAndTodosOfDate(date: LocalDate) {

        val startMillis =
            date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis =
            date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        taskViewModel.getTasksByDate(startMillis, endMillis)
            .observe(viewLifecycleOwner) { tasks ->

                if (currentFilter == FilterMode.ALL || currentFilter == FilterMode.TASK) {
                    rvTasks.visibility = View.VISIBLE
                    taskAdapter.setData(tasks)
                } else rvTasks.visibility = View.GONE

                if (currentFilter == FilterMode.ALL || currentFilter == FilterMode.TODO) {

                    val groupedTodos = mutableMapOf<String, MutableList<String>>()

                    tasks.forEach { task ->
                        val blocks = NotionBlockParser.fromJson(task.notesJson)
                        blocks.filter {
                            it.type == NotionBlock.Type.TODO &&
                                    it.deadline in startMillis until endMillis
                        }.forEach { todo ->
                            groupedTodos
                                .getOrPut(task.title) { mutableListOf() }
                                .add(todo.text)
                        }
                    }

                    rvTodos.visibility = View.VISIBLE
                    todoAdapter.setData(groupedTodos)

                } else rvTodos.visibility = View.GONE
            }
    }

    private fun openEditTask(task: Task) {
        val intent = Intent(requireContext(), TaskDetailActivity::class.java)
        intent.putExtra("task_id", task.getId())
        startActivityForResult(intent, REQUEST_EDIT_TASK)
    }

    private fun generateCalendarDays(month: YearMonth): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()

        val firstDay = month.atDay(1)
        val totalDays = month.lengthOfMonth()
        val dayOfWeekIndex = firstDay.dayOfWeek.value % 7

        val prevMonth = month.minusMonths(1)
        val prevMonthDays = prevMonth.lengthOfMonth()
        for (i in 1..dayOfWeekIndex) {
            val dayNum = prevMonthDays - (dayOfWeekIndex - i)
            days.add(CalendarDay(dayNum, false, prevMonth.atDay(dayNum)))
        }

        for (i in 1..totalDays) {
            days.add(CalendarDay(i, true, month.atDay(i)))
        }

        val nextMonth = month.plusMonths(1)
        val remain = 42 - days.size
        for (i in 1..remain) {
            days.add(CalendarDay(i, false, nextMonth.atDay(i)))
        }

        return days
    }
}
