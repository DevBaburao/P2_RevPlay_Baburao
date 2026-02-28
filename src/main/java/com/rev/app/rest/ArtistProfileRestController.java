package com.rev.app.rest;

import com.rev.app.dto.ArtistProfileDTO;
import com.rev.app.service.ArtistProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistProfileRestController {

    private final ArtistProfileService artistProfileService;

    public ArtistProfileRestController(ArtistProfileService artistProfileService) {
        this.artistProfileService = artistProfileService;
    }

    @PostMapping
    public ResponseEntity<ArtistProfileDTO> createArtistProfile(@RequestBody ArtistProfileDTO dto) {
        ArtistProfileDTO created = artistProfileService.createArtistProfile(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistProfileDTO> getArtistProfileById(@PathVariable Long id) {
        ArtistProfileDTO profile = artistProfileService.getArtistProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<List<ArtistProfileDTO>> getAllArtistProfiles() {
        List<ArtistProfileDTO> profiles = artistProfileService.getAllArtistProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ArtistProfileDTO> getArtistProfileByUserId(@PathVariable Long userId) {
        ArtistProfileDTO profile = artistProfileService.getArtistProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }
}
