package com.rev.app.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class FrontendController {

    @Autowired
    private com.rev.app.repository.SongRepository songRepository;

    @Autowired
    private com.rev.app.repository.PlaylistRepository playlistRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(@org.springframework.web.bind.annotation.RequestParam(required = false) String keyword,
            org.springframework.ui.Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("songs", songRepository
                    .findByNameContainingIgnoreCaseOrArtistContainingIgnoreCase(keyword.trim(), keyword.trim()));
            model.addAttribute("keyword", keyword.trim());
        } else {
            model.addAttribute("songs", songRepository.findAll());
        }
        model.addAttribute("playlists", playlistRepository.findAll());
        return "dashboard";
    }
}
