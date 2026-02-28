package com.rev.app.service;

import com.rev.app.dto.ArtistProfileDTO;
import com.rev.app.entity.ArtistProfile;
import com.rev.app.entity.User;
import com.rev.app.mapper.ArtistProfileMapper;
import com.rev.app.repository.ArtistProfileRepository;
import com.rev.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistProfileServiceImpl implements ArtistProfileService {

    private final ArtistProfileRepository artistProfileRepository;
    private final UserRepository userRepository;
    private final ArtistProfileMapper artistProfileMapper;

    public ArtistProfileServiceImpl(ArtistProfileRepository artistProfileRepository,
            UserRepository userRepository,
            ArtistProfileMapper artistProfileMapper) {
        this.artistProfileRepository = artistProfileRepository;
        this.userRepository = userRepository;
        this.artistProfileMapper = artistProfileMapper;
    }

    @Override
    public ArtistProfileDTO createArtistProfile(ArtistProfileDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

        ArtistProfile entity = artistProfileMapper.toEntity(dto, user);
        ArtistProfile savedEntity = artistProfileRepository.save(entity);

        return artistProfileMapper.toDto(savedEntity);
    }

    @Override
    public ArtistProfileDTO getArtistProfileById(Long id) {
        ArtistProfile entity = artistProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ArtistProfile not found with ID: " + id));
        return artistProfileMapper.toDto(entity);
    }

    @Override
    public List<ArtistProfileDTO> getAllArtistProfiles() {
        return artistProfileRepository.findAll().stream()
                .map(artistProfileMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ArtistProfileDTO getArtistProfileByUserId(Long userId) {
        ArtistProfile entity = artistProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ArtistProfile not found for User ID: " + userId));
        return artistProfileMapper.toDto(entity);
    }
}
