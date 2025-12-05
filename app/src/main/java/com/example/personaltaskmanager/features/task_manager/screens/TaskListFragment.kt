package com.example.personaltaskmanager.features.task_manager.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.task_manager.data.model.Task
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel
import com.example.personaltaskmanager.features.task_manager.screens.workspace.TaskWorkspaceActivity

class TaskListFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var rvTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var spinnerFilter: Spinner
    private lateinit var btnAddSmall: ImageButton
    private lateinit var fabAdd: View

    private val REQUEST_ADD_TASK = 2001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.feature_task_manager_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTasks = view.findViewById(R.id.rv_tasks)
        spinnerFilter = view.findViewById(R.id.spinner_filter)
        btnAddSmall = view.findViewById(R.id.btn_add_small)
        fabAdd = view.findViewById(R.id.fab_add_task)

        viewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        setupRecycler()

        fabAdd.setOnClickListener { openAddTask() }
        btnAddSmall.setOnClickListener { openAddTask() }

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.setData(tasks)
        }
    }

    private fun setupRecycler() {
        adapter = TaskAdapter(
            { task -> openEditTask(task) },
            { task -> viewModel.deleteTask(task) },
            { task, done -> viewModel.toggleCompleted(task, done) }
        )

        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = adapter
    }

    private fun openAddTask() {
        val intent = Intent(requireContext(), TaskDetailActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_TASK)
    }

    private fun openEditTask(task: Task) {
        val intent = Intent(requireContext(), TaskWorkspaceActivity::class.java)
        intent.putExtra("task_id", task.id)
        startActivity(intent)
    }
}
