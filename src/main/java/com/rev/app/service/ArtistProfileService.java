package com.rev.app.service;

import com.rev.app.dto.ArtistProfileDTO;

import java.util.List;
import com.rev.app.dto.ArtistDashboardDTO;

public interface ArtistProfileService {
    ArtistProfileDTO createArtistProfile(ArtistProfileDTO dto);

    ArtistProfileDTO getArtistProfileById(Long id);

    List<ArtistProfileDTO> getAllArtistProfiles();

    ArtistProfileDTO getArtistProfileByUserId(Long userId);

    ArtistDashboardDTO getArtistDashboard();
}
