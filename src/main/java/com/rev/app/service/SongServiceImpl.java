package com.rev.app.service;

import com.rev.app.dto.SongDTO;
import com.rev.app.entity.Album;
import com.rev.app.entity.ArtistProfile;
import com.rev.app.entity.Genre;
import com.rev.app.entity.Song;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.SongMapper;
import com.rev.app.repository.AlbumRepository;
import com.rev.app.repository.ArtistProfileRepository;
import com.rev.app.repository.GenreRepository;
import com.rev.app.repository.SongRepository;
import com.rev.app.repository.ListeningHistoryRepository;
import com.rev.app.dto.SongPlayCountDTO;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final GenreRepository genreRepository;
    private final AlbumRepository albumRepository;
    private final ListeningHistoryRepository listeningHistoryRepository;
    private final SongMapper songMapper;

    public SongServiceImpl(SongRepository songRepository,
            ArtistProfileRepository artistProfileRepository,
            GenreRepository genreRepository,
            AlbumRepository albumRepository,
            ListeningHistoryRepository listeningHistoryRepository,
            SongMapper songMapper) {
        this.songRepository = songRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.genreRepository = genreRepository;
        this.albumRepository = albumRepository;
        this.listeningHistoryRepository = listeningHistoryRepository;
        this.songMapper = songMapper;
    }

    @Override
    public SongDTO createSong(SongDTO dto) {
        ArtistProfile artist = artistProfileRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + dto.getArtistId()));

        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + dto.getGenreId()));

        Song entity = songMapper.toEntity(dto, artist, genre);

        if (dto.getAlbumId() != null) {
            Album album = albumRepository.findById(dto.getAlbumId())
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with ID: " + id));
        if (entity.getIsDeleted() != null && entity.getIsDeleted() == 1) {
            throw new RuntimeException("Song is deleted");
        }
        return songMapper.toDto(entity);
    }

    @Override
    public Page<SongDTO> getAllSongs(Pageable pageable) {
        return songRepository.findByIsDeleted(0, pageable)
                .map(songMapper::toDto);
    }

    @Override
    public SongDTO updateSong(Long id, SongDTO dto) {
        Song existingEntity = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with ID: " + id));

        if (existingEntity.getIsDeleted() != null && existingEntity.getIsDeleted() == 1) {
            throw new RuntimeException("Cannot update a deleted song");
        }

        ArtistProfile artist = artistProfileRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + dto.getArtistId()));

        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + dto.getGenreId()));

        existingEntity.setArtist(artist);

        if (dto.getAlbumId() != null) {
            Album album = albumRepository.findById(dto.getAlbumId())
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
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
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        song.setIsDeleted(1);
        songRepository.save(song);
    }

    @Override
    public List<SongDTO> searchSongs(String title) {
        return songRepository.findByTitleContainingIgnoreCaseAndIsDeleted(title, 0).stream()
                .map(songMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongPlayCountDTO> getTopSongs(int limit) {
        return listeningHistoryRepository.findTopPlayedSongs(PageRequest.of(0, limit)).stream()
                .map(obj -> new SongPlayCountDTO(songMapper.toDto((Song) obj[0]), (Long) obj[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongPlayCountDTO> getTrendingSongs() {
        Timestamp sevenDaysAgo = Timestamp.from(Instant.now().minus(7, ChronoUnit.DAYS));
        return listeningHistoryRepository.findTrendingSongs(sevenDaysAgo, PageRequest.of(0, 50)).stream()
                .map(obj -> new SongPlayCountDTO(songMapper.toDto((Song) obj[0]), (Long) obj[1]))
                .collect(Collectors.toList());
    }
}
