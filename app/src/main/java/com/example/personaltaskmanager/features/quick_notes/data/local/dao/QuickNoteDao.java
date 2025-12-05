package com.example.personaltaskmanager.features.quick_notes.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personaltaskmanager.features.quick_notes.data.model.QuickNote;

import java.util.List;

@Dao
public interface QuickNoteDao {

    @Query("SELECT * FROM quick_notes WHERE taskId = :taskId ORDER BY createdAt DESC")
    LiveData<List<QuickNote>> getNotesByTask(int taskId);

    @Insert
    long insertNote(QuickNote note);

    @Delete
    void deleteNote(QuickNote note);

    @Query("UPDATE quick_notes SET content = :content WHERE id = :id")
    void updateNote(int id, String content);
}
