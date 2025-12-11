package com.example.personaltaskmanager.features.authentication.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.personaltaskmanager.features.authentication.data.local.dao.UserDao;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;

import java.util.concurrent.Executors;

/**
 * Room Database dành riêng cho Authentication.
 * - Lưu user offline: login, register, auto-login.
 * - Không chia sẻ với các module khác.
 */
@Database(
        entities = {UserEntity.class},
        version = 2,      // 🔥 nhớ tăng version (bắt buộc)
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

                            // =================================================
                            // SEED ADMIN USER (CHỈ CHẠY LẦN ĐẦU TẠO DATABASE)
                            // =================================================
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        UserEntity admin = new UserEntity();
                                        admin.username = "admin";
                                        admin.email = "admin@gmail.com";
                                        admin.password = "123456";
                                        admin.role = "admin";

                                        getInstance(context)
                                                .userDao()
                                                .insertUser(admin);
                                    });
                                }
                            })

                            // =================================================
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}