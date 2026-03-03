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
            @org.springframework.web.bind.annotation.RequestParam(required = false) String description,
            @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "PUBLIC") String privacy) {

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        // we'll quickly resolve user here, but userRepository is down below.
        // Best to just inject it above or use a local fetch via userService if needed.
        // Wait, userRepository is autowired in the class.
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        com.rev.app.dto.PlaylistDTO dto = new com.rev.app.dto.PlaylistDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrivacy(privacy);
        if (user != null) {
            dto.setUserId(user.getId());
        }
        playlistService.createPlaylist(dto);
        return "redirect:/my-playlists";
    }

    @Autowired
    private com.rev.app.repository.PlaylistRepository playlistRepository;

    @Autowired
    private com.rev.app.repository.UserRepository userRepository;

    @org.springframework.web.bind.annotation.GetMapping("/my-playlists")
    public String myPlaylists(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            model.addAttribute("playlists", playlistRepository.findByUserAndIsDeleted(user, 0));
            model.addAttribute("followedPlaylists", playlistRepository.findByFollowersContainingAndIsDeleted(user, 0));
        }
        return "my-playlists";
    }

    @org.springframework.web.bind.annotation.GetMapping("/playlists/public")
    public String publicPlaylists(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("playlists",
                playlistRepository.findByPrivacyAndIsDeleted(com.rev.app.entity.Playlist.Privacy.PUBLIC, 0));
        model.addAttribute("user", user);
        return "public-playlists";
    }

    @PostMapping("/playlists/{playlistId}/follow")
    public String followPlaylist(@PathVariable Long playlistId) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            playlistService.followPlaylist(playlistId, user.getId());
        }
        return "redirect:/playlists/public";
    }

    @PostMapping("/playlists/{playlistId}/unfollow")
    public String unfollowPlaylist(@PathVariable Long playlistId,
            @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "my-playlists") String source) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            playlistService.unfollowPlaylist(playlistId, user.getId());
        }
        return source.equals("public") ? "redirect:/playlists/public" : "redirect:/my-playlists";
    }
}
