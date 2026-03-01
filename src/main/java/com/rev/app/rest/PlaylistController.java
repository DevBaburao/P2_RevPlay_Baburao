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

    @PostMapping("/playlists/{playlistId}/remove/{songId}")
    public String removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return "redirect:/my-playlists";
    }

    @PostMapping("/playlists/create")
    public String createPlaylist(@org.springframework.web.bind.annotation.RequestParam String name,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String description) {
        com.rev.app.dto.PlaylistDTO dto = new com.rev.app.dto.PlaylistDTO();
        dto.setName(name);
        dto.setDescription(description);
        playlistService.createPlaylist(dto);
        return "redirect:/my-playlists";
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
