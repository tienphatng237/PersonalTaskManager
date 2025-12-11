package com.example.personaltaskmanager.features.admin.data;

import android.content.Context;
import com.example.personaltaskmanager.features.authentication.data.local.AuthDatabase;
import com.example.personaltaskmanager.features.authentication.data.local.dao.UserDao;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;
import java.util.List;

public class AdminRepository {

    private final UserDao userDao;

    public AdminRepository(Context context) {
        userDao = AuthDatabase.getInstance(context).userDao();
    }

    public List<UserEntity> getAllUsers() {
        return userDao.getAllUsers();
    }
}
