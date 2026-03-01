package com.rev.app.mapper;

import com.rev.app.dto.SongDTO;
import com.rev.app.entity.Genre;
import com.rev.app.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public SongDTO toDto(Song entity) {
        if (entity == null) {
            return null;
        }

        SongDTO dto = new SongDTO();
        dto.setId(entity.getId());
        if (entity.getArtist() != null) {
            dto.setArtistId(entity.getArtist().getId());
        }
        if (entity.getAlbum() != null) {
            dto.setAlbumId(entity.getAlbum().getId());
        } else {
            dto.setAlbumId(null);
        }
        if (entity.getGenre() != null) {
            dto.setGenreId(entity.getGenre().getId());
        }
        dto.setTitle(entity.getTitle());
        dto.setDuration(entity.getDuration());
        dto.setAudioUrl(entity.getAudioUrl());
        dto.setFileSize(entity.getFileSize());
        dto.setReleaseDate(entity.getReleaseDate());
        dto.setVisibility(entity.getVisibility());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setIsDeleted(entity.getIsDeleted());

        return dto;
    }

    public Song toEntity(SongDTO dto, com.rev.app.entity.User artist, Genre genre) {
        if (dto == null) {
            return null;
        }

        Song entity = new Song();
        entity.setId(dto.getId());
        entity.setArtist(artist);
        entity.setAlbumId(dto.getAlbumId());
        entity.setGenre(genre);
        entity.setTitle(dto.getTitle());
        entity.setDuration(dto.getDuration());
        entity.setAudioUrl(dto.getAudioUrl());
        entity.setFileSize(dto.getFileSize());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setVisibility(dto.getVisibility());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : 0);

        return entity;
    }
}
