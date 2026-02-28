package com.rev.app.mapper;

import com.rev.app.dto.ArtistProfileDTO;
import com.rev.app.entity.ArtistProfile;
import com.rev.app.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ArtistProfileMapper {

    public ArtistProfileDTO toDto(ArtistProfile entity) {
        if (entity == null) {
            return null;
        }

        ArtistProfileDTO dto = new ArtistProfileDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        dto.setArtistName(entity.getArtistName());
        dto.setGenreId(entity.getGenreId());
        dto.setBannerImage(entity.getBannerImage());
        dto.setInstagramLink(entity.getInstagramLink());
        dto.setTwitterLink(entity.getTwitterLink());
        dto.setYoutubeLink(entity.getYoutubeLink());
        dto.setSpotifyLink(entity.getSpotifyLink());
        dto.setWebsiteLink(entity.getWebsiteLink());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public ArtistProfile toEntity(ArtistProfileDTO dto, User user) {
        if (dto == null) {
            return null;
        }

        ArtistProfile entity = new ArtistProfile();
        entity.setId(dto.getId());
        entity.setUser(user);
        entity.setArtistName(dto.getArtistName());
        entity.setGenreId(dto.getGenreId());
        entity.setBannerImage(dto.getBannerImage());
        entity.setInstagramLink(dto.getInstagramLink());
        entity.setTwitterLink(dto.getTwitterLink());
        entity.setYoutubeLink(dto.getYoutubeLink());
        entity.setSpotifyLink(dto.getSpotifyLink());
        entity.setWebsiteLink(dto.getWebsiteLink());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}
