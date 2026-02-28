package com.rev.app.service;

import com.rev.app.dto.SongDTO;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SongService {
    SongDTO createSong(SongDTO dto);

    SongDTO getSongById(Long id);

    Page<SongDTO> getAllSongs(Pageable pageable);

    SongDTO updateSong(Long id, SongDTO dto);

    void deleteSong(Long id);

    List<SongDTO> searchSongs(String title);
}
