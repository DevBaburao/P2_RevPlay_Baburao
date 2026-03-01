package com.rev.app.service;

import com.rev.app.dto.ArtistProfileDTO;
import com.rev.app.entity.ArtistProfile;
import com.rev.app.entity.User;
import com.rev.app.mapper.ArtistProfileMapper;
import com.rev.app.repository.ArtistProfileRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.repository.FavoriteRepository;
import com.rev.app.repository.ListeningHistoryRepository;
import com.rev.app.repository.SongRepository;
import com.rev.app.mapper.SongMapper;
import com.rev.app.dto.ArtistDashboardDTO;
import com.rev.app.dto.SongPlayCountDTO;
import com.rev.app.entity.Song;
import com.rev.app.exception.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistProfileServiceImpl implements ArtistProfileService {

    private final ArtistProfileRepository artistProfileRepository;
    private final UserRepository userRepository;
    private final ArtistProfileMapper artistProfileMapper;
    private final SongRepository songRepository;
    private final ListeningHistoryRepository listeningHistoryRepository;
    private final FavoriteRepository favoriteRepository;
    private final SongMapper songMapper;

    public ArtistProfileServiceImpl(ArtistProfileRepository artistProfileRepository,
            UserRepository userRepository,
            ArtistProfileMapper artistProfileMapper,
            SongRepository songRepository,
            ListeningHistoryRepository listeningHistoryRepository,
            FavoriteRepository favoriteRepository,
            SongMapper songMapper) {
        this.artistProfileRepository = artistProfileRepository;
        this.userRepository = userRepository;
        this.artistProfileMapper = artistProfileMapper;
        this.songRepository = songRepository;
        this.listeningHistoryRepository = listeningHistoryRepository;
        this.favoriteRepository = favoriteRepository;
        this.songMapper = songMapper;
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

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or unauthenticated"));
    }

    @Override
    public ArtistDashboardDTO getArtistDashboard() {
        User user = getAuthenticatedUser();
        ArtistProfile artist = artistProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist profile not found for current user"));

        Long totalSongs = songRepository.countByArtistAndIsDeleted(artist.getUser(), 0);
        Long totalPlays = listeningHistoryRepository.countTotalPlaysByArtist(artist.getUser());
        Long totalFavorites = favoriteRepository.countTotalFavoritesByArtist(artist.getUser());

        List<Object[]> mostPlayedResult = listeningHistoryRepository.findMostPlayedSongByArtist(artist.getUser(),
                PageRequest.of(0, 1));
        SongPlayCountDTO mostPlayedSong = null;
        if (!mostPlayedResult.isEmpty()) {
            Object[] obj = mostPlayedResult.get(0);
            mostPlayedSong = new SongPlayCountDTO(songMapper.toDto((Song) obj[0]), (Long) obj[1]);
        }

        ArtistDashboardDTO dashboard = new ArtistDashboardDTO();
        dashboard.setTotalSongs(totalSongs != null ? totalSongs : 0L);
        dashboard.setTotalPlays(totalPlays != null ? totalPlays : 0L);
        dashboard.setTotalFavorites(totalFavorites != null ? totalFavorites : 0L);
        dashboard.setMostPlayedSong(mostPlayedSong);

        return dashboard;
    }
}
