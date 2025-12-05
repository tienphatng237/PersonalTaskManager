package com.example.personaltaskmanager.features.authentication.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.personaltaskmanager.features.authentication.data.local.dao.UserDao;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;

/**
 * Room Database dành riêng cho Authentication.
 * - Lưu user offline: login, register, auto-login.
 * - Không chia sẻ với các module khác.
 */
@Database(
        entities = {UserEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AuthDatabase extends RoomDatabase {

    private static volatile AuthDatabase INSTANCE;

    public abstract UserDao userDao();

    /**
     * Singleton instance — thread-safe double-check locking.
     */
    public static AuthDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AuthDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AuthDatabase.class,
                                    "auth_db"
                            )
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()   // ⚠ Giữ nguyên theo yêu cầu
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
