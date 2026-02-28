package com.rev.app.service;

import com.rev.app.dto.PlaylistDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaylistService {
    PlaylistDTO createPlaylist(PlaylistDTO dto);

    PlaylistDTO addSongToPlaylist(Long playlistId, Long songId);

    PlaylistDTO removeSongFromPlaylist(Long playlistId, Long songId);

    PlaylistDTO getPlaylistById(Long id);

    Page<PlaylistDTO> getAllPlaylists(Pageable pageable);

    void deletePlaylist(Long id);

    List<PlaylistDTO> searchPlaylists(String name);
}
