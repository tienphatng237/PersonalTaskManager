package com.example.personaltaskmanager.features.authentication.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity đại diện dữ liệu người dùng trong Room DB (AuthDatabase).
 * Lưu username, email, password dưới dạng local offline.
 */
@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String email;
    public String password;

    public UserEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /** Room yêu cầu constructor rỗng trong vài trường hợp */
    public UserEntity() {}
}
