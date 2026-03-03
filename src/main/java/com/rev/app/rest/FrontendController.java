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
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("artists", userRepository.findByRole(com.rev.app.entity.Role.ARTIST));
        model.addAttribute("albums", albumRepository.findByIsDeleted(0));

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("user", user);

        java.util.List<com.rev.app.entity.Song> topSongs = songRepository.findTop5ByIsDeletedOrderByPlayCountDesc(0);
        model.addAttribute("topSongs", topSongs);

        return "dashboard";
    }

    @Autowired
    private com.rev.app.repository.ListeningHistoryRepository listeningHistoryRepository;

    @GetMapping("/songs/play/{id}")
    public String playSong(@org.springframework.web.bind.annotation.PathVariable Long id,
            jakarta.servlet.http.HttpSession session) {
        com.rev.app.entity.Song song = songRepository.findById(id).orElse(null);
        if (song != null) {
            com.rev.app.entity.PlaybackQueue queue = (com.rev.app.entity.PlaybackQueue) session.getAttribute("queue");
            if (queue != null && queue.getSongIds().contains(id)) {
                queue.setCurrentIndex(queue.getSongIds().indexOf(id));
            }

            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);

            String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            if (username != null && !username.equals("anonymousUser")) {
                com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    com.rev.app.entity.ListeningHistory history = new com.rev.app.entity.ListeningHistory();
                    history.setUser(user);
                    history.setSong(song);
                    listeningHistoryRepository.save(history);
                }
            }

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
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        model.addAttribute("genres", genreRepository.findAll());
        java.util.List<com.rev.app.entity.Album> artistAlbums = albumRepository.findByArtistUsername(username).stream()
                .filter(a -> a.getIsDeleted() == 0)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("albums", artistAlbums);
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
                    model.addAttribute("albums", albumRepository.findByArtistUsername(username).stream()
                            .filter(a -> a.getIsDeleted() == 0).collect(java.util.stream.Collectors.toList()));
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
        java.util.List<com.rev.app.entity.Album> artistAlbums = albumRepository.findByArtistUsername(username).stream()
                .filter(a -> a.getIsDeleted() == 0)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("albums", artistAlbums);
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

    // Duplicate profile method removed

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

    // --- ALBUM ENDPOINTS ---

    @GetMapping("/albums")
    public String browseAlbums(org.springframework.ui.Model model) {
        java.util.List<com.rev.app.entity.Album> albums = albumRepository.findByIsDeleted(0);
        model.addAttribute("albums", albums);
        return "albums";
    }

    @GetMapping("/albums/{id}")
    public String viewAlbum(@org.springframework.web.bind.annotation.PathVariable Long id,
            org.springframework.ui.Model model) {
        com.rev.app.entity.Album album = albumRepository.findById(id).orElse(null);
        if (album != null && album.getIsDeleted() == 0) {
            model.addAttribute("album", album);
            // Get active songs only
            java.util.List<com.rev.app.entity.Song> activeSongs = album.getSongs().stream()
                    .filter(s -> s.getIsDeleted() == 0)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("songs", activeSongs);
            return "album-detail";
        }
        return "redirect:/albums";
    }

    @GetMapping("/my-albums")
    public String myAlbums(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getRole() == com.rev.app.entity.Role.ARTIST) {
            java.util.List<com.rev.app.entity.Album> myAlbums = albumRepository.findByArtistUsername(username).stream()
                    .filter(a -> a.getIsDeleted() == 0)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("albums", myAlbums);
            return "my-albums";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/albums/create")
    public String createAlbumForm(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getRole() == com.rev.app.entity.Role.ARTIST) {
            return "create-album";
        }
        return "redirect:/dashboard";
    }

    @org.springframework.web.bind.annotation.PostMapping("/albums/create")
    public String createAlbum(
            @org.springframework.web.bind.annotation.RequestParam String name,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String description,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate releaseDate,
            @org.springframework.web.bind.annotation.RequestParam(value = "coverImageFile", required = false) org.springframework.web.multipart.MultipartFile coverImageFile) {

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && user.getRole() == com.rev.app.entity.Role.ARTIST) {
            com.rev.app.entity.Album album = new com.rev.app.entity.Album();
            album.setArtist(user);
            album.setName(name);
            album.setDescription(description);
            album.setReleaseDate(releaseDate);

            if (coverImageFile != null && !coverImageFile.isEmpty()) {
                String coverUrl = saveUploadedFile(coverImageFile);
                if (coverUrl != null) {
                    album.setCoverImage(coverUrl);
                }
            }
            albumRepository.save(album);
            return "redirect:/my-albums";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/albums/edit/{id}")
    public String editAlbumForm(@org.springframework.web.bind.annotation.PathVariable Long id,
            org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.Album album = albumRepository.findById(id).orElse(null);

        if (album != null && album.getArtist().getUsername().equals(username)) {
            model.addAttribute("album", album);
            return "edit-album";
        }
        return "redirect:/my-albums";
    }

    @org.springframework.web.bind.annotation.PostMapping("/albums/edit/{id}")
    public String updateAlbum(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam String name,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String description,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate releaseDate,
            @org.springframework.web.bind.annotation.RequestParam(value = "coverImageFile", required = false) org.springframework.web.multipart.MultipartFile coverImageFile) {

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.Album album = albumRepository.findById(id).orElse(null);

        if (album != null && album.getArtist().getUsername().equals(username)) {
            album.setName(name);
            album.setDescription(description);
            album.setReleaseDate(releaseDate);

            if (coverImageFile != null && !coverImageFile.isEmpty()) {
                String coverUrl = saveUploadedFile(coverImageFile);
                if (coverUrl != null) {
                    album.setCoverImage(coverUrl);
                }
            }
            albumRepository.save(album);
        }
        return "redirect:/my-albums";
    }

    @GetMapping("/albums/delete/{id}")
    public String deleteAlbum(@org.springframework.web.bind.annotation.PathVariable Long id,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.Album album = albumRepository.findById(id).orElse(null);

        if (album != null && album.getArtist().getUsername().equals(username)) {
            long activeSongsCount = album.getSongs().stream().filter(s -> s.getIsDeleted() == 0).count();
            if (activeSongsCount > 0) {
                redirectAttrs.addFlashAttribute("error", "Cannot delete album. It still contains active songs.");
            } else {
                album.setIsDeleted(1);
                albumRepository.save(album);
                redirectAttrs.addFlashAttribute("success", "Album deleted successfully.");
            }
        }
        return "redirect:/my-albums";
    }

    @GetMapping("/history")
    public String getListeningHistory(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            java.util.List<com.rev.app.entity.ListeningHistory> history = listeningHistoryRepository
                    .findTop50ByUserOrderByPlayedAtDesc(user);
            model.addAttribute("history", history);
        }
        return "history";
    }

    @GetMapping("/history/all")
    public String getAllListeningHistory(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            java.util.List<com.rev.app.entity.ListeningHistory> history = listeningHistoryRepository
                    .findByUserOrderByPlayedAtDesc(user);
            model.addAttribute("history", history);
        }
        return "history-all";
    }

    @org.springframework.web.bind.annotation.PostMapping("/history/clear")
    public String clearListeningHistory(org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            listeningHistoryRepository.deleteByUser(user);
            redirectAttrs.addFlashAttribute("success", "Listening history cleared successfully.");
        }
        return "redirect:/history";
    }

    @Autowired
    private com.rev.app.repository.FavoriteRepository favoriteRepository;

    @GetMapping("/artist/dashboard")
    public String artistDashboard(org.springframework.ui.Model model) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.rev.app.entity.User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || user.getRole() != com.rev.app.entity.Role.ARTIST) {
            return "redirect:/dashboard";
        }

        long totalSongs = songRepository.countByArtistAndIsDeleted(user, 0);
        long totalAlbums = albumRepository.countByArtistAndIsDeleted(user, 0);

        Long totalPlaysObj = listeningHistoryRepository.countTotalPlaysByArtist(user);
        long totalPlays = totalPlaysObj != null ? totalPlaysObj : 0L;

        Long totalFavoritesObj = favoriteRepository.countTotalFavoritesByArtist(user);
        long totalFavorites = totalFavoritesObj != null ? totalFavoritesObj : 0L;

        java.util.List<com.rev.app.entity.Song> allSongs = songRepository
                .findByArtistAndIsDeletedOrderByPlayCountDesc(user, 0);
        java.util.List<com.rev.app.entity.Song> topSongs = allSongs.stream().limit(5).toList();

        org.springframework.data.domain.Pageable top5 = org.springframework.data.domain.PageRequest.of(0, 5);
        java.util.List<Object[]> topListeners = listeningHistoryRepository.findTopListenersByArtist(user, top5);

        model.addAttribute("totalSongs", totalSongs);
        model.addAttribute("totalAlbums", totalAlbums);
        model.addAttribute("totalPlays", totalPlays);
        model.addAttribute("totalFavorites", totalFavorites);
        model.addAttribute("topSongs", topSongs);
        model.addAttribute("topListeners", topListeners);

        return "artist-dashboard";
    }
}
