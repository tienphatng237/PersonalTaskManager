package com.example.personaltaskmanager.features.authentication.data.mapper;

import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;
import com.example.personaltaskmanager.features.authentication.data.model.User;

/**
 * Mapper chuyển giữa:
 *  - UserEntity (Local Room)
 *  - User (Domain Model)
 *
 * Mục tiêu:
 *  - Tách biệt domain khỏi tầng database
 *  - Dễ mở rộng khi thêm Firebase DTO sau này
 */
public class UserMapper {

    /**
     * Convert Entity → Domain Model.
     */
    public static User toModel(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.username,
                entity.email,
                entity.password
        );
    }

    /**
     * Convert Domain Model → Entity để lưu DB.
     */
    public static UserEntity toEntity(User user) {
        if (user == null) return null;

        return new UserEntity(
                user.username,
                user.email,
                user.password
        );
    }
}
