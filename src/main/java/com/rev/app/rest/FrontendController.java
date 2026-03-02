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

    @Autowired
    private com.rev.app.repository.GenreRepository genreRepository;

    @Autowired
    private com.rev.app.repository.AlbumRepository albumRepository;

    @Autowired
    private com.rev.app.repository.ArtistProfileRepository artistProfileRepository;

    @Autowired
    private com.rev.app.repository.UserRepository userRepository;

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

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("user", user);

        java.util.List<com.rev.app.entity.Song> topSongs = songRepository.findTop5ByIsDeletedOrderByPlayCountDesc(0);
        model.addAttribute("topSongs", topSongs);

        return "dashboard";
    }

    @GetMapping("/songs/play/{id}")
    public String playSong(@org.springframework.web.bind.annotation.PathVariable Long id) {
        com.rev.app.entity.Song song = songRepository.findById(id).orElse(null);
        if (song != null) {
            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);
            return "redirect:" + song.getAudioUrl();
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/my-songs")
    public String mySongs(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            java.util.List<com.rev.app.entity.Song> mySongs = songRepository.findByArtist_IdAndIsDeleted(user.getId(),
                    0);
            model.addAttribute("songs", mySongs);
        }
        return "my-songs";
    }

    @GetMapping("/songs/create")
    public String createSongForm(org.springframework.ui.Model model) {
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("albums", albumRepository.findAll());
        return "create-song";
    }

    @org.springframework.web.bind.annotation.PostMapping("/songs/create")
    public String processCreateSong(
            @org.springframework.web.bind.annotation.ModelAttribute com.rev.app.entity.Song newSong,
            @org.springframework.web.bind.annotation.RequestParam("audioFile") org.springframework.web.multipart.MultipartFile audioFile,
            org.springframework.ui.Model model) {

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            newSong.setArtist(user);

            if (!audioFile.isEmpty()) {
                try {
                    String originalFilename = audioFile.getOriginalFilename();
                    String fileExtension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;

                    java.nio.file.Path uploadPath = java.nio.file.Paths.get(System.getProperty("user.dir"), "uploads");
                    if (!java.nio.file.Files.exists(uploadPath)) {
                        java.nio.file.Files.createDirectories(uploadPath);
                    }

                    java.nio.file.Path filePath = uploadPath.resolve(uniqueFilename);
                    java.nio.file.Files.copy(audioFile.getInputStream(), filePath,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    newSong.setAudioUrl("/uploads/" + uniqueFilename);

                } catch (java.io.IOException e) {
                    model.addAttribute("error", "Could not save audio file.");
                    model.addAttribute("genres", genreRepository.findAll());
                    model.addAttribute("albums", albumRepository.findAll());
                    return "create-song";
                }
            }

            songRepository.save(newSong);
        }

        return "redirect:/my-songs?created=true";
    }

    @GetMapping("/songs/edit/{id}")
    public String editSongForm(@org.springframework.web.bind.annotation.PathVariable Long id,
            org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        com.rev.app.entity.Song song = songRepository.findById(id).orElse(null);
        if (song == null || user == null || !song.getArtist().getId().equals(user.getId())) {
            return "redirect:/my-songs";
        }

        model.addAttribute("song", song);
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("albums", albumRepository.findAll());
        return "edit-song";
    }

    @org.springframework.web.bind.annotation.PostMapping("/songs/edit/{id}")
    public String processEditSong(@org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.ModelAttribute com.rev.app.entity.Song updatedSong) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        com.rev.app.entity.Song existingSong = songRepository.findById(id).orElse(null);
        if (existingSong != null && user != null && existingSong.getArtist().getId().equals(user.getId())) {
            existingSong.setTitle(updatedSong.getTitle());
            existingSong.setDuration(updatedSong.getDuration());
            existingSong.setReleaseDate(updatedSong.getReleaseDate());
            existingSong.setAudioUrl(updatedSong.getAudioUrl());
            existingSong.setGenre(updatedSong.getGenre());
            existingSong.setAlbum(updatedSong.getAlbum());
            songRepository.save(existingSong);
        }
        return "redirect:/my-songs";
    }

    @GetMapping("/songs/delete/{id}")
    public String deleteSong(@org.springframework.web.bind.annotation.PathVariable Long id) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        com.rev.app.entity.Song song = songRepository.findById(id).orElse(null);
        if (song != null && user != null && song.getArtist().getId().equals(user.getId())) {
            song.setIsDeleted(1);
            songRepository.save(song);
        }
        return "redirect:/my-songs";
    }

    @GetMapping("/songs/like/{id}")
    public String likeSong(@org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestHeader(value = "referer", required = false) String referer) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        com.rev.app.entity.Song song = songRepository.findById(id).orElse(null);

        if (user != null && song != null && !user.getLikedSongs().contains(song)) {
            user.getLikedSongs().add(song);
            userRepository.save(user); // saves both due to cascading or many-to-many
        }

        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    @GetMapping("/songs/unlike/{id}")
    public String unlikeSong(@org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestHeader(value = "referer", required = false) String referer) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        com.rev.app.entity.Song song = songRepository.findById(id).orElse(null);

        if (user != null && song != null && user.getLikedSongs().contains(song)) {
            user.getLikedSongs().remove(song);
            userRepository.save(user);
        }

        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    @GetMapping("/liked-songs")
    public String likedSongs(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            model.addAttribute("songs", user.getLikedSongs());
            model.addAttribute("playlists", playlistRepository.findAll());
            model.addAttribute("user", user);
        }

        return "liked-songs";
    }

    @GetMapping("/profile")
    public String viewProfile(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            model.addAttribute("user", user);
            // Count total favorites
            model.addAttribute("totalFavorites", user.getLikedSongs().size());

            if (user.getRole() == com.rev.app.entity.Role.ARTIST) {
                com.rev.app.entity.ArtistProfile artistProfile = artistProfileRepository.findByUserId(user.getId())
                        .orElse(null);
                model.addAttribute("artistProfile", artistProfile);
                // Note: count totalPlaylists, totalPlays for Artist here if needed
                return "artist-profile";
            }

            // Note: count totalPlaylists for formatting
            long playlistCount = playlistRepository.findAll().stream().count();
            model.addAttribute("totalPlaylists", playlistCount);

            return "profile";
        }
        return "redirect:/login";
    }

    @GetMapping("/artist/{username}")
    public String viewPublicArtistProfile(@org.springframework.web.bind.annotation.PathVariable String username,
            org.springframework.ui.Model model) {
        com.rev.app.entity.User artistUser = userRepository.findByUsername(username).orElse(null);
        if (artistUser != null && artistUser.getRole() == com.rev.app.entity.Role.ARTIST) {
            model.addAttribute("artistUser", artistUser);
            com.rev.app.entity.ArtistProfile artistProfile = artistProfileRepository.findByUserId(artistUser.getId())
                    .orElse(null);
            model.addAttribute("artistProfile", artistProfile);
            return "public-artist";
        }
        return "redirect:/dashboard"; // Not found or not an artist
    }

    @org.springframework.web.bind.annotation.PostMapping("/profile/update")
    public String updateProfile(
            @org.springframework.web.bind.annotation.RequestParam String displayName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String bio) {

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            user.setDisplayName(displayName);
            user.setBio(bio);
            userRepository.save(user);
        }
        return "redirect:/profile";
    }

    @org.springframework.web.bind.annotation.PostMapping("/profile/update-artist")
    public String updateArtistProfile(
            @org.springframework.web.bind.annotation.RequestParam String artistName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long genreId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String bio,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String instagramLink,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String twitterLink,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String youtubeLink,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String spotifyLink,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String websiteLink,
            @org.springframework.web.bind.annotation.RequestParam(value = "profilePictureFile", required = false) org.springframework.web.multipart.MultipartFile profilePictureFile,
            @org.springframework.web.bind.annotation.RequestParam(value = "bannerImageFile", required = false) org.springframework.web.multipart.MultipartFile bannerImageFile) {

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && user.getRole() == com.rev.app.entity.Role.ARTIST) {
            user.setBio(bio);

            if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
                String ppUrl = saveUploadedFile(profilePictureFile);
                if (ppUrl != null) {
                    user.setProfilePicture(ppUrl);
                }
            }
            userRepository.save(user);

            com.rev.app.entity.ArtistProfile artistProfile = artistProfileRepository.findByUserId(user.getId())
                    .orElse(new com.rev.app.entity.ArtistProfile());
            if (artistProfile.getUser() == null) {
                artistProfile.setUser(user);
            }
            artistProfile.setArtistName(artistName);
            artistProfile.setGenreId(genreId);
            artistProfile.setInstagramLink(instagramLink);
            artistProfile.setTwitterLink(twitterLink);
            artistProfile.setYoutubeLink(youtubeLink);
            artistProfile.setSpotifyLink(spotifyLink);
            artistProfile.setWebsiteLink(websiteLink);

            if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
                String bannerUrl = saveUploadedFile(bannerImageFile);
                if (bannerUrl != null) {
                    artistProfile.setBannerImage(bannerUrl);
                }
            }

            artistProfileRepository.save(artistProfile);
        }
        return "redirect:/profile";
    }

    private String saveUploadedFile(org.springframework.web.multipart.MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(System.getProperty("user.dir"), "uploads");
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }
            java.nio.file.Path filePath = uploadPath.resolve(uniqueFilename);
            java.nio.file.Files.copy(file.getInputStream(), filePath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + uniqueFilename;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
