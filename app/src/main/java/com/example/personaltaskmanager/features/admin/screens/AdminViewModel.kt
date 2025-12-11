package com.example.personaltaskmanager.features.admin.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.personaltaskmanager.features.admin.data.AdminRepository

class AdminViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AdminRepository(app)

    val resultText = MutableLiveData<String>()

    /** GET ALL USERS */
    fun getAllUsers() {
        val list = repo.getAllUsers()

        if (list.isEmpty()) {
            resultText.value = "Không có user nào."
            return
        }

        val sb = StringBuilder()
        list.forEach {
            sb.append("ID: ${it.id}\n")
            sb.append("Username: ${it.username}\n")
            sb.append("Email: ${it.email}\n")
            sb.append("Role: ${it.role}\n")
            sb.append("----------------------\n")
        }

        resultText.value = sb.toString()
    }

    /** GET USER BY ID */
    fun getUserById(id: Int) {
        val u = repo.getUserById(id)

        resultText.value = if (u == null) {
            "Không tìm thấy user ID = $id"
        } else {
            """
                ID: ${u.id}
                Username: ${u.username}
                Email: ${u.email}
                Role: ${u.role}
            """.trimIndent()
        }
    }

    /** INSERT USER */
    fun insertUser(username: String, email: String, password: String) {

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            resultText.value = "Dữ liệu không hợp lệ!"
            return
        }

        val ok = repo.insertUser(username, email, password)

        resultText.value = if (ok) {
            "Thêm user thành công!"
        } else {
            "Thêm thất bại!"
        }
    }
}
