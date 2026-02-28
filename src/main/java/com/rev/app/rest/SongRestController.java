package com.rev.app.rest;

import com.rev.app.dto.SongDTO;
import com.rev.app.service.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import com.rev.app.dto.SongPlayCountDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/songs")
@Tag(name = "Song Management", description = "Endpoints for managing songs")
public class SongRestController {

    private final SongService songService;

    public SongRestController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping
    @Operation(summary = "Create a new song")
    public ResponseEntity<SongDTO> createSong(@Valid @RequestBody SongDTO dto) {
        SongDTO created = songService.createSong(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all songs with pagination")
    public ResponseEntity<Page<SongDTO>> getAllSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<SongDTO> songs = songService.getAllSongs(pageable);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a song by ID")
    public ResponseEntity<SongDTO> getSongById(@PathVariable Long id) {
        SongDTO song = songService.getSongById(id);
        return ResponseEntity.ok(song);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing song")
    public ResponseEntity<SongDTO> updateSong(@PathVariable Long id, @Valid @RequestBody SongDTO dto) {
        SongDTO updated = songService.updateSong(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a song (soft delete)")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search for a song by title")
    public ResponseEntity<List<SongDTO>> searchSongs(@RequestParam String title) {
        List<SongDTO> songs = songService.searchSongs(title);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/top")
    @Operation(summary = "Get top played songs")
    public ResponseEntity<List<SongPlayCountDTO>> getTopSongs(@RequestParam(defaultValue = "10") int limit) {
        List<SongPlayCountDTO> topSongs = songService.getTopSongs(limit);
        return ResponseEntity.ok(topSongs);
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending songs (highest plays in last 7 days)")
    public ResponseEntity<List<SongPlayCountDTO>> getTrendingSongs() {
        List<SongPlayCountDTO> trendingSongs = songService.getTrendingSongs();
        return ResponseEntity.ok(trendingSongs);
    }
}
