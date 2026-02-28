package com.rev.app.mapper;

import com.rev.app.dto.UserDTO;
import com.rev.app.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        // Intentionally omitting password for viewing
        dto.setRole(user.getRole());
        dto.setDisplayName(user.getDisplayName());
        dto.setBio(user.getBio());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsActive(user.getIsActive());

        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setDisplayName(dto.getDisplayName());
        user.setBio(dto.getBio());
        user.setProfilePicture(dto.getProfilePicture());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        user.setIsActive(dto.getIsActive());

        return user;
    }
}
