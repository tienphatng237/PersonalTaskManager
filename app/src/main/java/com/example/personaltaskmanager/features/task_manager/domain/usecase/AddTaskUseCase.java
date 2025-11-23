package com.example.personaltaskmanager.features.task_manager.domain.usecase;

import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository;

public class AddTaskUseCase {

    private final TaskRepository repository;

    public AddTaskUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    public long execute(Task task) {
        return repository.addTask(task);
    }
}
