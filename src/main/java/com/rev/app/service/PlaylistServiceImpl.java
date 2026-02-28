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

import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
    }

    @Override
    public PlaylistDTO createPlaylist(PlaylistDTO dto) {
        Playlist playlist = new Playlist();
        playlist.setName(dto.getName());
        playlist.setDescription(dto.getDescription());

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

        return dto;
    }
}
