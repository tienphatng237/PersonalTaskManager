package com.example.personaltaskmanager.features.task_manager.data.repository;

import android.content.Context;

import com.example.personaltaskmanager.features.task_manager.data.local.dao.TaskDao;
import com.example.personaltaskmanager.features.task_manager.data.local.db.AppDatabase;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;

import java.util.List;

public class TaskRepository {

    private final TaskDao taskDao;

    public TaskRepository(Context context) {
        this.taskDao = AppDatabase.getInstance(context).taskDao();
    }

    public List<Task> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public long addTask(Task task) {
        return taskDao.insertTask(task);
    }

    public void updateTask(Task task) {
        taskDao.updateTask(task);
    }

    public void deleteTask(Task task) {
        taskDao.deleteTask(task);
    }
}
