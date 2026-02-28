package com.rev.app.service;

import com.rev.app.dto.GenreDTO;
import com.rev.app.entity.Genre;
import com.rev.app.mapper.GenreMapper;
import com.rev.app.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    public GenreDTO createGenre(GenreDTO dto) {
        Genre entity = genreMapper.toEntity(dto);
        Genre savedEntity = genreRepository.save(entity);
        return genreMapper.toDto(savedEntity);
    }

    @Override
    public GenreDTO getGenreById(Long id) {
        Genre entity = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found with ID: " + id));
        return genreMapper.toDto(entity);
    }

    @Override
    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public GenreDTO updateGenre(Long id, GenreDTO dto) {
        Genre existingEntity = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found with ID: " + id));

        existingEntity.setName(dto.getName());
        Genre updatedEntity = genreRepository.save(existingEntity);

        return genreMapper.toDto(updatedEntity);
    }

    @Override
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found with ID: " + id);
        }
        genreRepository.deleteById(id);
    }
}
