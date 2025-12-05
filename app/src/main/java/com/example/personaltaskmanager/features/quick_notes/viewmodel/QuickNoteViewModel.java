package com.example.personaltaskmanager.features.quick_notes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.quick_notes.data.model.QuickNote;
import com.example.personaltaskmanager.features.quick_notes.data.repository.QuickNoteRepository;

import java.util.List;

public class QuickNoteViewModel extends AndroidViewModel {

    private final QuickNoteRepository repo;

    public QuickNoteViewModel(@NonNull Application app) {
        super(app);
        repo = new QuickNoteRepository(app);
    }

    public LiveData<List<QuickNote>> getNotes(int taskId) {
        return repo.getNotes(taskId);
    }

    public void addNote(int taskId, String content) {
        repo.addNote(new QuickNote(taskId, content, System.currentTimeMillis()));
    }

    public void updateNote(int id, String content) {
        repo.updateNote(id, content);
    }

    public void deleteNote(QuickNote note) {
        repo.deleteNote(note);
    }
}
