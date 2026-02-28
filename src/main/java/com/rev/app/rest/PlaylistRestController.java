package com.rev.app.rest;

import com.rev.app.dto.PlaylistDTO;
import com.rev.app.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@Tag(name = "Playlist Management", description = "Endpoints for managing playlists")
public class PlaylistRestController {

    private final PlaylistService playlistService;

    public PlaylistRestController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    @Operation(summary = "Create a new playlist")
    public ResponseEntity<PlaylistDTO> createPlaylist(@Valid @RequestBody PlaylistDTO dto) {
        PlaylistDTO created = playlistService.createPlaylist(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    @Operation(summary = "Add a song to a playlist")
    public ResponseEntity<PlaylistDTO> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        PlaylistDTO updated = playlistService.addSongToPlaylist(playlistId, songId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    @Operation(summary = "Remove a song from a playlist")
    public ResponseEntity<PlaylistDTO> removeSongFromPlaylist(@PathVariable Long playlistId,
            @PathVariable Long songId) {
        PlaylistDTO updated = playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a playlist by ID")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long id) {
        PlaylistDTO playlist = playlistService.getPlaylistById(id);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping
    @Operation(summary = "Get all playlists with pagination")
    public ResponseEntity<Page<PlaylistDTO>> getAllPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<PlaylistDTO> playlists = playlistService.getAllPlaylists(pageable);
        return ResponseEntity.ok(playlists);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a playlist (soft delete)")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search for a playlist by name")
    public ResponseEntity<List<PlaylistDTO>> searchPlaylists(@RequestParam String name) {
        List<PlaylistDTO> playlists = playlistService.searchPlaylists(name);
        return ResponseEntity.ok(playlists);
    }
}
