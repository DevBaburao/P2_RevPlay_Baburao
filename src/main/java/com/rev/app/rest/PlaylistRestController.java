package com.rev.app.rest;

import com.rev.app.dto.PlaylistDTO;
import com.rev.app.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistRestController {

    private final PlaylistService playlistService;

    public PlaylistRestController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO dto) {
        PlaylistDTO created = playlistService.createPlaylist(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<PlaylistDTO> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        PlaylistDTO updated = playlistService.addSongToPlaylist(playlistId, songId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<PlaylistDTO> removeSongFromPlaylist(@PathVariable Long playlistId,
            @PathVariable Long songId) {
        PlaylistDTO updated = playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long id) {
        PlaylistDTO playlist = playlistService.getPlaylistById(id);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping
    public ResponseEntity<Page<PlaylistDTO>> getAllPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<PlaylistDTO> playlists = playlistService.getAllPlaylists(pageable);
        return ResponseEntity.ok(playlists);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }
}
