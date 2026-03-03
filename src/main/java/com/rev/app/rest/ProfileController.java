package com.rev.app.rest;

import com.rev.app.entity.User;
import com.rev.app.repository.UserRepository;
import com.rev.app.repository.ListeningHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListeningHistoryRepository listeningHistoryRepository;

    @Autowired
    private com.rev.app.repository.ArtistProfileRepository artistProfileRepository;

    @Autowired
    private com.rev.app.repository.PlaylistRepository playlistRepository;

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);

            int likedSongsCount = user.getLikedSongs() != null ? user.getLikedSongs().size() : 0;
            long historyCount = listeningHistoryRepository.countByUser(user);

            model.addAttribute("likedSongsCount", likedSongsCount);
            model.addAttribute("historyCount", historyCount);
            model.addAttribute("totalFavorites", likedSongsCount);

            if (user.getRole() == com.rev.app.entity.Role.ARTIST) {
                com.rev.app.entity.ArtistProfile artistProfile = artistProfileRepository.findByUserId(user.getId())
                        .orElse(null);
                model.addAttribute("artistProfile", artistProfile);
                return "artist-profile";
            }

            long playlistCount = playlistRepository.findAll().stream().count();
            model.addAttribute("totalPlaylists", playlistCount);
        } else {
            return "redirect:/login";
        }
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String bio,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            if (displayName != null) {
                user.setDisplayName(displayName);
            }
            if (bio != null) {
                user.setBio(bio);
            }
            userRepository.save(user);
            redirectAttrs.addFlashAttribute("success", "Profile updated successfully!");
        }
        return "redirect:/profile";
    }
}
