package com.rev.app.rest;

import com.rev.app.dto.LoginRequestDTO;
import com.rev.app.dto.RegisterRequestDTO;
import com.rev.app.entity.Role;
import com.rev.app.entity.User;
import com.rev.app.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.rev.app.repository.ArtistProfileRepository artistProfileRepository;

    public AuthRestController(AuthenticationManager authenticationManager, UserRepository userRepository,
            PasswordEncoder passwordEncoder, com.rev.app.repository.ArtistProfileRepository artistProfileRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.artistProfileRepository = artistProfileRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() != null
                && (request.getRole().equalsIgnoreCase("ARTIST") || request.getRole().equalsIgnoreCase("USER"))) {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } else {
            user.setRole(Role.USER); // Default role
        }

        user.setDisplayName(request.getDisplayName());
        user.setBio(request.getBio());

        User savedUser = userRepository.save(user);

        // If registering as an ARTIST, dynamically initialize their ArtistProfile
        if (savedUser.getRole() == Role.ARTIST) {
            com.rev.app.entity.ArtistProfile artistProfile = new com.rev.app.entity.ArtistProfile();
            artistProfile.setUser(savedUser);
            artistProfile
                    .setArtistName(request.getArtistName() != null ? request.getArtistName() : request.getUsername());
            artistProfile.setInstagramLink(request.getInstagramUrl());
            artistProfile.setTwitterLink(request.getTwitterUrl());
            artistProfile.setYoutubeLink(request.getYoutubeUrl());
            artistProfile.setWebsiteLink(request.getWebsiteUrl());

            // Try to parse primaryGenre as an ID safely, otherwise leave null
            if (request.getPrimaryGenre() != null && !request.getPrimaryGenre().trim().isEmpty()) {
                try {
                    artistProfile.setGenreId(Long.parseLong(request.getPrimaryGenre()));
                } catch (NumberFormatException ignored) {
                }
            }

            artistProfileRepository.save(artistProfile);
        }

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpServletRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", sc);

        return new ResponseEntity<>("User logged in successfully", HttpStatus.OK);
    }
}
