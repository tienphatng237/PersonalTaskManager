package com.example.personaltaskmanager.features.authentication.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.personaltaskmanager.features.authentication.data.local.AuthDatabase;
import com.example.personaltaskmanager.features.authentication.data.local.dao.UserDao;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;
import com.example.personaltaskmanager.features.authentication.data.mapper.UserMapper;
import com.example.personaltaskmanager.features.authentication.data.model.User;

/**
 * AuthRepository
 * ---------------------
 * Xử lý logic Authentication local:
 *   - Login (validate user/password)
 *   - Register (kiểm tra trùng username)
 *   - Lưu trạng thái đăng nhập (SharedPreferences)
 *   - Logout
 *
 * Đây là Local Repository.
 * Khi tích hợp Firebase, lớp này sẽ được bổ sung RemoteAuthDataSource,
 * nhưng API hiện tại vẫn giữ nguyên để không ảnh hưởng lên UI / ViewModel.
 */
public class AuthRepository {

    private final UserDao userDao;
    private final SharedPreferences prefs;

    public AuthRepository(Context context) {
        userDao = AuthDatabase.getInstance(context).userDao();
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
    }

    /**
     * LOGIN LOCAL
     */
    public User login(String username, String password) {

        UserEntity u = userDao.getUserByUsername(username);

        if (u == null) return null;
        if (!u.password.equals(password)) return null;

        prefs.edit()
                .putString("current_user", username)
                .apply();

        // không đổi logic, chỉ trả User
        return UserMapper.toModel(u);
    }

    /**
     * REGISTER LOCAL
     */
    public boolean register(User user) {

        if (userDao.countUsername(user.username) > 0) {
            return false;
        }

        userDao.insertUser(UserMapper.toEntity(user));
        return true;
    }

    /**
     * GET CURRENT USER
     */
    public User getCurrentUser() {
        String username = prefs.getString("current_user", null);
        if (username == null) return null;

        UserEntity e = userDao.getUserByUsername(username);
        if (e == null) return null;

        return UserMapper.toModel(e);
    }

    /**
     * LOGOUT
     */
    public void logout() {
        prefs.edit().remove("current_user").apply();
    }
}
