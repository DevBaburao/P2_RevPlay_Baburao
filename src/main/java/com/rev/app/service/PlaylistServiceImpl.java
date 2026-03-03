package com.rev.app.service;

import com.rev.app.dto.PlaylistDTO;
import com.rev.app.entity.Playlist;
import com.rev.app.entity.Song;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.PlaylistRepository;
import com.rev.app.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final com.rev.app.repository.UserRepository userRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository,
            com.rev.app.repository.UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PlaylistDTO createPlaylist(PlaylistDTO dto) {
        Playlist playlist = new Playlist();
        playlist.setName(dto.getName());
        playlist.setDescription(dto.getDescription());
        playlist.setPrivacy(
                dto.getPrivacy() != null && dto.getPrivacy().equalsIgnoreCase("PRIVATE") ? Playlist.Privacy.PRIVATE
                        : Playlist.Privacy.PUBLIC);

        if (dto.getUserId() != null) {
            playlist.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        }

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return mapToDTO(savedPlaylist);
    }

    @Override
    public PlaylistDTO addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        playlist.getSongs().add(song);
        Playlist savedPlaylist = playlistRepository.save(playlist);

        return mapToDTO(savedPlaylist);
    }

    @Override
    public PlaylistDTO removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        playlist.getSongs().removeIf(song -> song.getId().equals(songId));
        Playlist savedPlaylist = playlistRepository.save(playlist);

        return mapToDTO(savedPlaylist);
    }

    @Override
    public PlaylistDTO getPlaylistById(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (playlist.getIsDeleted() != null && playlist.getIsDeleted() == 1) {
            throw new ResourceNotFoundException("Playlist not found");
        }

        return mapToDTO(playlist);
    }

    @Override
    public Page<PlaylistDTO> getAllPlaylists(Pageable pageable) {
        return playlistRepository.findByIsDeleted(0, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public void deletePlaylist(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        playlist.setIsDeleted(1);
        playlistRepository.save(playlist);
    }

    @Override
    public List<PlaylistDTO> searchPlaylists(String name) {
        return playlistRepository.findByNameContainingIgnoreCaseAndIsDeleted(name, 0).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PlaylistDTO mapToDTO(Playlist playlist) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setDescription(playlist.getDescription());

        if (playlist.getSongs() != null) {
            dto.setSongIds(
                    playlist.getSongs()
                            .stream()
                            .map(Song::getId)
                            .collect(Collectors.toList()));
        }

        if (playlist.getUser() != null) {
            dto.setUserId(playlist.getUser().getId());
        }
        if (playlist.getPrivacy() != null) {
            dto.setPrivacy(playlist.getPrivacy().name());
        }
        if (playlist.getFollowers() != null) {
            dto.setFollowerCount(playlist.getFollowers().size());
        }

        return dto;
    }

    @Override
    public List<PlaylistDTO> getMyPlaylists(Long userId) {
        com.rev.app.entity.User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return List.of();
        return playlistRepository.findByUserAndIsDeleted(user, 0).stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistDTO> getPublicPlaylists() {
        return playlistRepository.findByPrivacyAndIsDeleted(Playlist.Privacy.PUBLIC, 0).stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistDTO> getFollowedPlaylists(Long userId) {
        com.rev.app.entity.User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return List.of();
        return playlistRepository.findByFollowersContainingAndIsDeleted(user, 0).stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistDTO followPlaylist(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
        com.rev.app.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (playlist.getPrivacy() == Playlist.Privacy.PUBLIC && !playlist.getFollowers().contains(user)) {
            playlist.getFollowers().add(user);
            playlistRepository.save(playlist);
        }
        return mapToDTO(playlist);
    }

    @Override
    public PlaylistDTO unfollowPlaylist(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
        com.rev.app.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (playlist.getFollowers().contains(user)) {
            playlist.getFollowers().remove(user);
            playlistRepository.save(playlist);
        }
        return mapToDTO(playlist);
    }
}
