package com.rev.app.service;

import com.rev.app.dto.GenreDTO;

import java.util.List;

public interface GenreService {
    GenreDTO createGenre(GenreDTO dto);

    GenreDTO getGenreById(Long id);

    List<GenreDTO> getAllGenres();

    GenreDTO updateGenre(Long id, GenreDTO dto);

    void deleteGenre(Long id);
}
