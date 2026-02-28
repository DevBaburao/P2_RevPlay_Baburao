package com.rev.app.rest;

import com.rev.app.dto.SongDTO;
import com.rev.app.entity.Favorite;
import com.rev.app.entity.Song;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.SongMapper;
import com.rev.app.repository.FavoriteRepository;
import com.rev.app.repository.SongRepository;
import com.rev.app.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorite Songs", description = "Endpoints for user favorite songs")
public class FavoriteRestController {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final SongMapper songMapper;

    public FavoriteRestController(FavoriteRepository favoriteRepository, UserRepository userRepository,
            SongRepository songRepository, SongMapper songMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.songMapper = songMapper;
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or unauthenticated"));
    }

    @PostMapping("/{songId}")
    @Operation(summary = "Mark song as favorite")
    public ResponseEntity<Void> addFavorite(@PathVariable Long songId) {
        User user = getAuthenticatedUser();
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (song.getIsDeleted() != null && song.getIsDeleted() == 1) {
            throw new ResourceNotFoundException("Song not found");
        }

        if (favoriteRepository.findByUserAndSong(user, song).isEmpty()) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setSong(song);
            favoriteRepository.save(favorite);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{songId}")
    @Operation(summary = "Remove song from favorites")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long songId) {
        User user = getAuthenticatedUser();
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        favoriteRepository.findByUserAndSong(user, song).ifPresent(favoriteRepository::delete);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all favorite songs of logged-in user")
    public ResponseEntity<List<SongDTO>> getFavorites() {
        User user = getAuthenticatedUser();
        List<SongDTO> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(favorite -> songMapper.toDto(favorite.getSong()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(favorites);
    }
}
