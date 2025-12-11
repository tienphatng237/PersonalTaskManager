package com.example.personaltaskmanager.features.authentication.data.model;

/**
 * Model dùng trong logic (Domain layer).
 *
 * Đây là lớp đại diện cho đối tượng User mà UseCase và Repository
 * sẽ thao tác — KHÁC với UserEntity (Room database).
 *
 * Không chứa annotation của Room để giữ sạch Domain Layer.
 */
public class User {

    public String username;
    public String email;
    public String password;

    // thêm role — KHÔNG sửa comment cũ
    public String role;

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // giữ nguyên constructor cũ để không lỗi code
    public User(String username, String email, String password) {
        this(username, email, password, "user");
    }
}
