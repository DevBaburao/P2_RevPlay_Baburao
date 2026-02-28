package com.rev.app.mapper;

import com.rev.app.dto.GenreDTO;
import com.rev.app.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreDTO toDto(Genre entity) {
        if (entity == null) {
            return null;
        }

        GenreDTO dto = new GenreDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }

    public Genre toEntity(GenreDTO dto) {
        if (dto == null) {
            return null;
        }

        Genre entity = new Genre();
        entity.setId(dto.getId());
        entity.setName(dto.getName());

        return entity;
    }
}
