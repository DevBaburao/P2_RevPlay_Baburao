package com.rev.app.rest;

import com.rev.app.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @PostMapping("/playlists/{playlistId}/songs/{songId}")
    public String addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        return "redirect:/dashboard";
    }

    @Autowired
    private com.rev.app.repository.PlaylistRepository playlistRepository;

    @org.springframework.web.bind.annotation.GetMapping("/my-playlists")
    public String myPlaylists(org.springframework.ui.Model model) {
        // Fetching all playlists directly to access their songs
        model.addAttribute("playlists", playlistRepository.findAll());
        return "my-playlists";
    }
}
