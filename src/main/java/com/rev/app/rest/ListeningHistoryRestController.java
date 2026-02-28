package com.rev.app.rest;

import com.rev.app.dto.SongDTO;
import com.rev.app.entity.ListeningHistory;
import com.rev.app.entity.Song;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.SongMapper;
import com.rev.app.repository.ListeningHistoryRepository;
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
@RequestMapping("/api/history")
@Tag(name = "Listening History", description = "Endpoints for user listening history")
public class ListeningHistoryRestController {

    private final ListeningHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final SongMapper songMapper;

    public ListeningHistoryRestController(ListeningHistoryRepository historyRepository, UserRepository userRepository,
            SongRepository songRepository, SongMapper songMapper) {
        this.historyRepository = historyRepository;
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
    @Operation(summary = "Save play record for logged-in user")
    public ResponseEntity<Void> addHistory(@PathVariable Long songId) {
        User user = getAuthenticatedUser();
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (song.getIsDeleted() != null && song.getIsDeleted() == 1) {
            throw new ResourceNotFoundException("Song not found");
        }

        ListeningHistory history = new ListeningHistory();
        history.setUser(user);
        history.setSong(song);
        historyRepository.save(history);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/recent")
    @Operation(summary = "Return last 50 songs sorted by playedAt desc")
    public ResponseEntity<List<SongDTO>> getRecentHistory() {
        User user = getAuthenticatedUser();
        List<SongDTO> history = historyRepository.findTop50ByUserOrderByPlayedAtDesc(user).stream()
                .map(h -> songMapper.toDto(h.getSong()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }
}
