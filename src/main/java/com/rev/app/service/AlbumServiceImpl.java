package com.rev.app.service;

import com.rev.app.dto.AlbumDTO;
import com.rev.app.entity.Album;
import com.rev.app.entity.ArtistProfile;
import com.rev.app.repository.AlbumRepository;
import com.rev.app.repository.ArtistProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistProfileRepository artistProfileRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository, ArtistProfileRepository artistProfileRepository) {
        this.albumRepository = albumRepository;
        this.artistProfileRepository = artistProfileRepository;
    }

    @Override
    public AlbumDTO createAlbum(AlbumDTO dto) {
        ArtistProfile artist = artistProfileRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Album album = new Album();
        album.setArtist(artist);
        album.setName(dto.getName());
        album.setDescription(dto.getDescription());
        album.setReleaseDate(dto.getReleaseDate());
        album.setCoverImage(dto.getCoverImage());
        album.setCreatedAt(LocalDateTime.now());
        album.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : 0);

        Album savedAlbum = albumRepository.save(album);
        return mapToDTO(savedAlbum);
    }

    @Override
    public List<AlbumDTO> getAllAlbums() {
        return albumRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AlbumDTO getAlbumById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        return mapToDTO(album);
    }

    @Override
    public AlbumDTO updateAlbum(Long id, AlbumDTO dto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        ArtistProfile artist = artistProfileRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        album.setArtist(artist);
        album.setName(dto.getName());
        album.setDescription(dto.getDescription());
        album.setReleaseDate(dto.getReleaseDate());
        album.setCoverImage(dto.getCoverImage());
        // Do not update createdAt
        album.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : album.getIsDeleted());

        Album updatedAlbum = albumRepository.save(album);
        return mapToDTO(updatedAlbum);
    }

    @Override
    public void deleteAlbum(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new RuntimeException("Album not found");
        }
        albumRepository.deleteById(id);
    }

    // Manual mapping as requested
    private AlbumDTO mapToDTO(Album album) {
        if (album == null) {
            return null;
        }

        AlbumDTO dto = new AlbumDTO();
        dto.setId(album.getId());
        if (album.getArtist() != null) {
            dto.setArtistId(album.getArtist().getId());
        }
        dto.setName(album.getName());
        dto.setDescription(album.getDescription());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setCoverImage(album.getCoverImage());
        dto.setIsDeleted(album.getIsDeleted());

        return dto;
    }
}
