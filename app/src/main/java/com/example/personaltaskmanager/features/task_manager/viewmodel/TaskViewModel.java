package com.example.personaltaskmanager.features.task_manager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository;
import com.example.personaltaskmanager.features.task_manager.domain.usecase.AddTaskUseCase;
import com.example.personaltaskmanager.features.task_manager.domain.usecase.GetTasksUseCase;

import java.util.List;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final GetTasksUseCase getTasksUseCase;
    private final AddTaskUseCase addTaskUseCase;

    public MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);

        repository = new TaskRepository(application);
        getTasksUseCase = new GetTasksUseCase(repository);
        addTaskUseCase = new AddTaskUseCase(repository);

        loadTasks();
    }

    public void loadTasks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> tasks = getTasksUseCase.execute();
            tasksLiveData.postValue(tasks);
        });
    }

    public void addTask(String title, String description) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = new Task(title, description, System.currentTimeMillis());
            addTaskUseCase.execute(task);
            loadTasks(); // refresh list
        });
    }
}
