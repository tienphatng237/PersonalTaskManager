package com.example.personaltaskmanager.features.authentication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;

/**
 * DAO xử lý CRUD cho bảng Users trong local DB (Room).
 * Dùng cho Login, Register, AutoLogin, Logout.
 */
@Dao
public interface UserDao {

    /**
     * Thêm mới user.
     * Nếu username trùng → tự động REPLACE user cũ.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity user);

    /**
     * Lấy user theo username (login).
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    UserEntity getUserByUsername(String username);

    /**
     * Check username đã tồn tại trong DB chưa.
     */
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countUsername(String username);

    /**
     * Lấy user đầu tiên (phục vụ Auto-login).
     */
    @Query("SELECT * FROM users LIMIT 1")
    UserEntity getFirstUser();

    /**
     * Xóa toàn bộ user (dùng cho Logout hoặc Reset DB).
     */
    @Query("DELETE FROM users")
    void deleteAll();
}
