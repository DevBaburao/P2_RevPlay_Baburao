package com.rev.app.service;

import com.rev.app.dto.SongDTO;

import java.util.List;

public interface SongService {
    SongDTO createSong(SongDTO dto);

    SongDTO getSongById(Long id);

    List<SongDTO> getAllSongs();

    SongDTO updateSong(Long id, SongDTO dto);

    void deleteSong(Long id);
}
