package com.rev.app.service;

import com.rev.app.dto.AlbumDTO;

import java.util.List;

public interface AlbumService {
    AlbumDTO createAlbum(AlbumDTO dto);

    List<AlbumDTO> getAllAlbums();

    AlbumDTO getAlbumById(Long id);

    AlbumDTO updateAlbum(Long id, AlbumDTO dto);

    void deleteAlbum(Long id);
}
