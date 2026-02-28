package com.rev.app.service;

import com.rev.app.dto.SongDTO;
import com.rev.app.entity.Album;
import com.rev.app.entity.ArtistProfile;
import com.rev.app.entity.Genre;
import com.rev.app.entity.Song;
import com.rev.app.mapper.SongMapper;
import com.rev.app.repository.AlbumRepository;
import com.rev.app.repository.ArtistProfileRepository;
import com.rev.app.repository.GenreRepository;
import com.rev.app.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final GenreRepository genreRepository;
    private final AlbumRepository albumRepository;
    private final SongMapper songMapper;

    public SongServiceImpl(SongRepository songRepository,
            ArtistProfileRepository artistProfileRepository,
            GenreRepository genreRepository,
            AlbumRepository albumRepository,
            SongMapper songMapper) {
        this.songRepository = songRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.genreRepository = genreRepository;
        this.albumRepository = albumRepository;
        this.songMapper = songMapper;
    }

    @Override
    public SongDTO createSong(SongDTO dto) {
        ArtistProfile artist = artistProfileRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found with ID: " + dto.getArtistId()));

        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found with ID: " + dto.getGenreId()));

        Song entity = songMapper.toEntity(dto, artist, genre);

        if (dto.getAlbumId() != null) {
            Album album = albumRepository.findById(dto.getAlbumId())
                    .orElseThrow(() -> new RuntimeException("Album not found"));
            entity.setAlbum(album);
        } else {
            entity.setAlbum(null);
        }

        Song savedEntity = songRepository.save(entity);
        return songMapper.toDto(savedEntity);
    }

    @Override
    public SongDTO getSongById(Long id) {
        Song entity = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with ID: " + id));
        if (entity.getIsDeleted() != null && entity.getIsDeleted() == 1) {
            throw new RuntimeException("Song is deleted");
        }
        return songMapper.toDto(entity);
    }

    @Override
    public List<SongDTO> getAllSongs() {
        return songRepository.findByIsDeleted(0).stream()
                .map(songMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SongDTO updateSong(Long id, SongDTO dto) {
        Song existingEntity = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with ID: " + id));

        if (existingEntity.getIsDeleted() != null && existingEntity.getIsDeleted() == 1) {
            throw new RuntimeException("Cannot update a deleted song");
        }

        ArtistProfile artist = artistProfileRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found with ID: " + dto.getArtistId()));

        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found with ID: " + dto.getGenreId()));

        existingEntity.setArtist(artist);

        if (dto.getAlbumId() != null) {
            Album album = albumRepository.findById(dto.getAlbumId())
                    .orElseThrow(() -> new RuntimeException("Album not found"));
            existingEntity.setAlbum(album);
        } else {
            existingEntity.setAlbum(null);
        }

        existingEntity.setGenre(genre);
        existingEntity.setTitle(dto.getTitle());
        existingEntity.setDuration(dto.getDuration());
        existingEntity.setAudioUrl(dto.getAudioUrl());
        existingEntity.setFileSize(dto.getFileSize());
        existingEntity.setReleaseDate(dto.getReleaseDate());
        existingEntity.setVisibility(dto.getVisibility());
        existingEntity.setStatus(dto.getStatus());

        Song updatedEntity = songRepository.save(existingEntity);
        return songMapper.toDto(updatedEntity);
    }

    @Override
    public void deleteSong(Long id) {
        Song existingEntity = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with ID: " + id));

        // Soft delete
        existingEntity.setIsDeleted(1);
        songRepository.save(existingEntity);
    }
}
