package com.rev.app.service;

import com.rev.app.dto.PlaylistDTO;

public interface PlaylistService {
    PlaylistDTO createPlaylist(PlaylistDTO dto);

    PlaylistDTO addSongToPlaylist(Long playlistId, Long songId);

    PlaylistDTO removeSongFromPlaylist(Long playlistId, Long songId);

    PlaylistDTO getPlaylistById(Long id);
}
