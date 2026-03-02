package com.rev.app.rest;

import com.rev.app.entity.Song;
import com.rev.app.entity.Genre;
import com.rev.app.entity.User;
import com.rev.app.entity.Album;
import com.rev.app.repository.SongRepository;
import com.rev.app.repository.GenreRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long genre,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) Long albumId,
            @RequestParam(required = false) Integer year,
            Model model) {

        // Nullify empty keyword
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // Fetch filtered songs using the custom JPQL query
        List<Song> songs = songRepository.advancedSearch(keyword, genre, artistId, albumId, year);

        // Fetch lists for filter dropdowns
        List<Genre> allGenres = genreRepository.findAll();
        List<User> allArtists = userRepository.findByRole(com.rev.app.entity.Role.ARTIST);
        List<Album> allAlbums = albumRepository.findByIsDeleted(0);

        model.addAttribute("songs", songs);
        model.addAttribute("genres", allGenres);
        model.addAttribute("artists", allArtists);
        model.addAttribute("albums", allAlbums);

        // Return selected parameters to the view so filters remain active
        model.addAttribute("paramKeyword", keyword);
        model.addAttribute("paramGenre", genre);
        model.addAttribute("paramArtistId", artistId);
        model.addAttribute("paramAlbumId", albumId);
        model.addAttribute("paramYear", year);

        return "search-results";
    }
}
