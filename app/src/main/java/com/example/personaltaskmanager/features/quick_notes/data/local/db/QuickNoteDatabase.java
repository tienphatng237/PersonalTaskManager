package com.example.personaltaskmanager.features.quick_notes.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.personaltaskmanager.features.quick_notes.data.local.dao.QuickNoteDao;
import com.example.personaltaskmanager.features.quick_notes.data.model.QuickNote;

@Database(entities = {QuickNote.class}, version = 1, exportSchema = false)
public abstract class QuickNoteDatabase extends RoomDatabase {

    private static volatile QuickNoteDatabase INSTANCE;

    public abstract QuickNoteDao quickNoteDao();

    public static QuickNoteDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (QuickNoteDatabase.class) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        QuickNoteDatabase.class,
                        "quick_note_db"
                ).fallbackToDestructiveMigration().build();
            }
        }
        return INSTANCE;
    }
}
