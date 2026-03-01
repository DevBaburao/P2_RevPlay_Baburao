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
    public String dashboard(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String keyword,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5") int size,
            org.springframework.ui.Model model) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<com.rev.app.entity.Song> songPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            songPage = songRepository.findByNameContainingIgnoreCaseOrArtistContainingIgnoreCase(keyword.trim(),
                    keyword.trim(), pageable);
            model.addAttribute("keyword", keyword.trim());
        } else {
            songPage = songRepository.findAll(pageable);
        }

        model.addAttribute("songs", songPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", songPage.getTotalPages());
        model.addAttribute("playlists", playlistRepository.findAll());
        return "dashboard";
    }
}
