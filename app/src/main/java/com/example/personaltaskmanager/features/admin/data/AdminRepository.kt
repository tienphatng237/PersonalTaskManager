package com.example.personaltaskmanager.features.admin.data

import android.content.Context
import com.example.personaltaskmanager.features.authentication.data.local.AuthDatabase
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity

class AdminRepository(context: Context) {

    private val userDao = AuthDatabase.getInstance(context).userDao()

    /** GET ALL USERS */
    fun getAllUsers(): List<UserEntity> {
        return userDao.getAllUsers()
    }

    /** GET USER BY ID (tối ưu nhưng không thay đổi kiến trúc DAO hiện tại) */
    fun getUserById(id: Int): UserEntity? {
        return userDao.getAllUsers().firstOrNull { it.id == id }
    }

    /** INSERT USER */
    fun insertUser(username: String, email: String, password: String): Boolean {
        return try {
            val entity = UserEntity(username, email, password)
            userDao.insertUser(entity)   // insert entity (Room sẽ tự sinh ID)
            true
        } catch (e: Exception) {
            false
        }
    }
}
