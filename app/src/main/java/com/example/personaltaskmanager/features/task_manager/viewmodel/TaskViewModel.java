package com.example.personaltaskmanager.features.task_manager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository;
import com.example.personaltaskmanager.features.task_manager.domain.usecase.AddTaskUseCase;
import com.example.personaltaskmanager.features.task_manager.domain.usecase.DeleteTaskUseCase;
import com.example.personaltaskmanager.features.task_manager.domain.usecase.GetTasksUseCase;
import com.example.personaltaskmanager.features.task_manager.domain.usecase.GetTasksByDateUseCase;

import java.util.List;

/**
 * TaskViewModel quản lý dữ liệu Task theo mô hình MVVM.
 * Giữ nguyên code cũ, chỉ bổ sung logic deadline + getTasksByDate().
 */
public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final GetTasksUseCase getTasksUseCase;
    private final AddTaskUseCase addTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    // NEW — use case lấy task theo ngày
    private final GetTasksByDateUseCase getTasksByDateUseCase;

    private final LiveData<List<Task>> allTasksLiveData;

    public TaskViewModel(@NonNull Application application) {
        super(application);

        repository = new TaskRepository(application);
        getTasksUseCase = new GetTasksUseCase(repository);
        addTaskUseCase = new AddTaskUseCase(repository);
        deleteTaskUseCase = new DeleteTaskUseCase(repository);

        getTasksByDateUseCase = new GetTasksByDateUseCase(repository);

        allTasksLiveData = getTasksUseCase.execute();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasksLiveData;
    }

    public Task getTaskById(int id) {
        return repository.getTaskById(id);
    }

    /**
     * ADD Task + deadline
     * Giữ nguyên logic cũ, chỉ bổ sung notesJson & tablesJson (để dọn chỗ cho 2 feature mới).
     */
    public void addTask(String title, String description, long deadline) {

        Task task = new Task(
                title,
                description,
                System.currentTimeMillis(), // createdAt
                deadline,
                "",   // notesJson — sẽ dùng cho feature Quick Notes
                ""    // tablesJson — sẽ dùng cho feature Tables
        );

        addTaskUseCase.execute(task);
    }

    /**
     * UPDATE Task + deadline
     */
    public void updateTask(Task task, String newTitle, String newDesc, long deadline) {
        task.setTitle(newTitle);
        task.setDescription(newDesc);
        task.setDeadline(deadline);
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        deleteTaskUseCase.execute(task);
    }

    /**
     * Toggle completed
     */
    public void toggleCompleted(Task task, boolean done) {
        task.setCompleted(done);
        repository.updateCompleted(task, done);
    }

    /**
     * NEW — Lấy task theo ngày
     */
    public LiveData<List<Task>> getTasksByDate(long start, long end) {
        return getTasksByDateUseCase.execute(start, end);
    }
}
