package com.example.personaltaskmanager.features.quick_notes.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.quick_notes.data.local.dao.QuickNoteDao;
import com.example.personaltaskmanager.features.quick_notes.data.local.db.QuickNoteDatabase;
import com.example.personaltaskmanager.features.quick_notes.data.model.QuickNote;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuickNoteRepository {

    private final QuickNoteDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public QuickNoteRepository(Context context) {
        dao = QuickNoteDatabase.getInstance(context).quickNoteDao();
    }

    public LiveData<List<QuickNote>> getNotes(int taskId) {
        return dao.getNotesByTask(taskId);
    }

    public void addNote(QuickNote note) {
        executor.execute(() -> dao.insertNote(note));
    }

    public void updateNote(int id, String content) {
        executor.execute(() -> dao.updateNote(id, content));
    }

    public void deleteNote(QuickNote note) {
        executor.execute(() -> dao.deleteNote(note));
    }
}
