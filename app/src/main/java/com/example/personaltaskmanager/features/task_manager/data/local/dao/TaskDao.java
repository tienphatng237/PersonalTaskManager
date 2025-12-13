package com.example.personaltaskmanager.features.task_manager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personaltaskmanager.features.task_manager.data.model.Task;

import java.util.List;

/**
 * DAO xử lý CRUD với Room DB.
 * Giữ nguyên toàn bộ code cũ, chỉ bổ sung userId vào query.
 */
@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    LiveData<List<Task>> getAllTasks(int userId);

    // SỬA: trả về LiveData để tránh query main thread
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    LiveData<Task> getTaskById(int taskId);

    @Insert
    long insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("UPDATE tasks SET isCompleted = :done WHERE id = :taskId")
    void updateCompleted(int taskId, boolean done);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND deadline BETWEEN :start AND :end ORDER BY deadline ASC")
    LiveData<List<Task>> getTasksByDate(int userId, long start, long end);
}
