package com.rev.app.service;

import com.rev.app.dto.ArtistProfileDTO;

import java.util.List;

public interface ArtistProfileService {
    ArtistProfileDTO createArtistProfile(ArtistProfileDTO dto);

    ArtistProfileDTO getArtistProfileById(Long id);

    List<ArtistProfileDTO> getAllArtistProfiles();

    ArtistProfileDTO getArtistProfileByUserId(Long userId);
}
