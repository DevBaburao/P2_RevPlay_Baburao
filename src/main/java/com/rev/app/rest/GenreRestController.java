package com.rev.app.rest;

import com.rev.app.dto.GenreDTO;
import com.rev.app.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreRestController {

    private final GenreService genreService;

    public GenreRestController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@RequestBody GenreDTO dto) {
        GenreDTO created = genreService.createGenre(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        List<GenreDTO> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable Long id) {
        GenreDTO genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable Long id, @RequestBody GenreDTO dto) {
        GenreDTO updated = genreService.updateGenre(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
